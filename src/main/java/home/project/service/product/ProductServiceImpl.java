package home.project.service.product;

import home.project.domain.common.QnA;
import home.project.domain.common.Review;
import home.project.domain.elasticsearch.ProductDocument;
import home.project.domain.member.Member;
import home.project.domain.member.MemberProduct;
import home.project.domain.product.Product;
import home.project.domain.product.ProductOrder;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.*;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.common.QnARepository;
import home.project.repository.common.ReviewRepository;
import home.project.repository.member.MemberProductRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.product.CategoryRepository;
import home.project.repository.product.ProductRepository;
import home.project.repository.product.WishListRepository;
import home.project.repositoryForElasticsearch.ProductElasticsearchRepository;
import home.project.service.member.MemberService;
import home.project.service.util.Converter;
import home.project.service.util.IndexToElasticsearch;
import home.project.service.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static home.project.service.util.CategoryMapper.getCode;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductElasticsearchRepository productElasticsearchRepository;
    private final Converter converter;
    private final IndexToElasticsearch indexToElasticsearch;
    private final ElasticsearchOperations elasticsearchOperations;
    private final MemberService memberService;
    private final MemberProductRepository memberProductRepository;
    private final PageUtil pageUtil;
    private final WishListRepository wishListRepository;
    private final QnARepository qnARepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void join(CreateProductRequestDTO createProductRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Long currentStock = createProductRequestDTO.getStock();
        Long currentSoldQuantity = createProductRequestDTO.getSoldQuantity();
        if (currentStock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        } else if (currentSoldQuantity < 0) {
            throw new IllegalStateException("판매량이 음수일 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDate = now.format(formatter);
        String timeStamp = formattedDate.substring(2, 4) + formattedDate.substring(6, 8) +
                formattedDate.substring(10, 11) + formattedDate.substring(12, 13);

        Product product = new Product();
        product.setName(createProductRequestDTO.getName());
        product.setBrand(createProductRequestDTO.getBrand());
        product.setCategory(categoryRepository.findByCode(createProductRequestDTO.getCategory())
                .orElseThrow(() -> new IdNotFoundException(createProductRequestDTO.getCategory() + " 카테고리가 없습니다.")));
        product.setStock(createProductRequestDTO.getStock());
        product.setProductNum(timeStamp + createProductRequestDTO.getBrand().charAt(0) + createProductRequestDTO.getName().charAt(0) + createProductRequestDTO.getCategory().toString());
        product.setSoldQuantity(createProductRequestDTO.getSoldQuantity());
        product.setPrice(createProductRequestDTO.getPrice());
        product.setDiscountRate(createProductRequestDTO.getDiscountRate());
        product.setDefectiveStock(createProductRequestDTO.getDefectiveStock());
        product.setDescription(createProductRequestDTO.getDescription());
        product.setCreateAt(LocalDateTime.now());
        product.setImageUrl(createProductRequestDTO.getImageUrl());
        product.setSize(createProductRequestDTO.getSize());
        product.setColor(createProductRequestDTO.getColor());

        boolean productNumExists = productRepository.existsByProductNum(product.getProductNum());
        if (productNumExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
        }

        productRepository.save(product);
        MemberProduct memberProduct = new MemberProduct();
        memberProduct.setProduct(product);
        memberProduct.setMember(member);
        memberProductRepository.save(memberProduct);

        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);

        try {
            indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
        private Member authentification(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);
        if(authentification == null){
            List<Long> likedProductIds =
        }else {

        }
    }*/
    @Override
    public ProductWithQnAAndReviewResponse findByIdReturnProductResponse(Long productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Pageable pageable = pageUtil.pageable(PageRequest.of(1, 5));
        Page<QnA> qnAs = qnARepository.findAllByProductId(productId, pageable);
        Page<Review> reviews = reviewRepository.findAllByProductId(productId, pageable);

        String email = authentication.getName();
        if (email.equals("anonymousUser")){
            return converter.convertFromProductToProductWithQnAAndReviewResponse(findById(productId), qnAs, reviews);
        }

        Member member = memberService.findByEmail(email);

        Product product = findById(productId);
        List<Long> likedProductIds = wishListRepository.findProductIdsByMemberId(member.getId());

/*
        kafkaEventProducerService.sendProductViewLog(productId);
*/
        return converter.convertFromProductToProductWithQnAAndReviewResponse2(product,likedProductIds, qnAs, reviews);
    }

    @Override
    public ProductWithQnAAndReviewResponseForManager findByIdReturnProductResponseForManager(Long productId) {
        Product product = findById(productId);
        Pageable pageable = pageUtil.pageable(PageRequest.of(1, 5));
        Page<QnA> qnAs = qnARepository.findAllByProductId(productId, pageable);
        Page<Review> reviews = reviewRepository.findAllByProductId(productId, pageable);
        return converter.convertFromProductToProductWithQnAAndReviewResponseForManager(product, qnAs, reviews);
    }

    @Override
    public Product findById(Long productId) {
        if (productId == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException(productId + "(으)로 등록된 상품이 없습니다."));
    }

    @Override
    public Product findByProductNum(String productNum){
        return productRepository.findByProductNum(productNum);
    }

    @Override
    public Page<ProductSimpleResponse> findAll(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAll(pageable);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        if (email.equals("anonymousUser")){

            return converter.convertFromPagedProductToPagedProductSimpleResponse(pagedProduct);
        }

        Member member = memberService.findByEmail(email);

        List<Long> likedProductIds = wishListRepository.findProductIdsByMemberId(member.getId());

        return converter.convertFromPagedProductToPagedProductSimpleResponse(pagedProduct,likedProductIds);
    }

    /*public Product getProductWithLikeStatus(Long memberId, Long productId) {
        Product product = findById(productId);

        List<Long> likedProductIds = wishListRepository.findProductIdsByMemberId(memberId);
        boolean isLiked = likedProductIds.contains(product.getId());
        product.setLiked(isLiked); // 좋아요 상태 설정

        return product;
    }

    public Page<Product> getProductsWithLikeStatus(Long memberId, Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAll(pageable);

        List<Long> likedProductIds = wishListRepository.findProductIdsByMemberId(memberId);

        pagedProduct.forEach(product -> {
            boolean isLiked = likedProductIds.contains(product.getId());
            product.setLiked(isLiked); // isLiked 상태 설정
        });

        return pagedProduct;
    }*/


    @Override
    public Page<ProductResponseForManager> findAllForManaging(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAll(pageable);
        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductSimpleResponseForManager> adminFindNewProduct(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findTop20LatestProducts(pageable);
        return converter.convertFromPagedProductToPagedProductSimpleResponseForManager(pagedProduct);
    }

    @Override
    public Page<ProductSimpleResponse> findNewProduct(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findTop20LatestProducts(pageable);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        if (email.equals("anonymousUser")){

            return converter.convertFromPagedProductToPagedProductSimpleResponse(pagedProduct);
        }

        Member member = memberService.findByEmail(email);

        List<Long> likedProductIds = wishListRepository.findProductIdsByMemberId(member.getId());

        return converter.convertFromPagedProductToPagedProductSimpleResponse(pagedProduct,likedProductIds);
    }

    @Override
    public Page<ProductResponse> findProducts(String brand, String category, String productName, String content, String color, String size, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        String categoryCode = null;

        if (category != null && !category.isEmpty()) {
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {
            categoryCode = getCode(content);
        }

        Page<Product> pagedProduct = productRepository.findProducts(brand, categoryCode, productName, content, color, size, pageable);

        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<ProductSimpleResponse> findProductsOnElastic(String brand, String category, String productName, String content, Pageable pageable) {


        Page<ProductDocument> pagedDocuments = productElasticsearchRepository.findProducts(brand, category, productName, content, pageable);

        Page<Product> pagedProduct = pagedDocuments.map(doc -> findById(doc.getId()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        if (email.equals("anonymousUser")){

            return converter.convertFromPagedProductToPagedProductSimpleResponse(pagedProduct);
        }

        Member member = memberService.findByEmail(email);

        List<Long> likedProductIds = wishListRepository.findProductIdsByMemberId(member.getId());

        return converter.convertFromPagedProductToPagedProductSimpleResponse(pagedProduct,likedProductIds);

    }

    @Override
    public Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content,String color, String size,  Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {
            categoryCode = getCode(content);
        }

        Page<Product> pagedProduct = productRepository.findProducts(brand, categoryCode, productName, content, color, size, pageable);

        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponseForManager> findProductsOnElasticForManaging(String brand, String category, String productName, String content, Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {//?
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {//?
            categoryCode = getCode(content);
        }

        Page<ProductDocument> pagedDocuments = productElasticsearchRepository.findProducts(brand, categoryCode, productName, content, pageable);

        Page<Product> pagedProduct = pagedDocuments.map(productDocument -> findById(productDocument.getId()));

        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseForManager> findSoldProducts(String brand, String category, String productName, String content, String color, String size, Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {
            categoryCode = getCode(content);
        }

        Page<Product> pagedProduct = productRepository.findProducts(brand, categoryCode, productName, content, color, size, pageable);

        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponse> findAllBySoldQuantity(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAllBySoldQuantity(pageable);
        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<String> brandList(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAllByOrderByBrandAsc(pageable);
        return converter.convertFromPagedProductToPagedBrand(pagedProduct);
    }

    @Override
    @Transactional
    public ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO) {

        boolean isCategoryModified = false;
        boolean isNameModified = false;
        boolean isBrandModified = false;

        Product existingProduct = findById(updateProductRequestDTO.getId());

        String newProductNum = reCreateProductNum(existingProduct.getProductNum(), updateProductRequestDTO);

        Product beforeUpdate = new Product();
        beforeUpdate.setId(existingProduct.getId());
        beforeUpdate.setName(existingProduct.getName());
        beforeUpdate.setBrand(existingProduct.getBrand());
        beforeUpdate.setCategory(existingProduct.getCategory());
        beforeUpdate.setStock(existingProduct.getStock());
        beforeUpdate.setProductNum(existingProduct.getProductNum());
        beforeUpdate.setSoldQuantity(existingProduct.getSoldQuantity());
        beforeUpdate.setPrice(existingProduct.getPrice());
        beforeUpdate.setDiscountRate(existingProduct.getDiscountRate());
        beforeUpdate.setDefectiveStock(existingProduct.getDefectiveStock());
        beforeUpdate.setDescription(existingProduct.getDescription());
        beforeUpdate.setCreateAt(existingProduct.getCreateAt());
        beforeUpdate.setImageUrl(existingProduct.getImageUrl());
        beforeUpdate.setSize(existingProduct.getSize());
        beforeUpdate.setColor(existingProduct.getColor());

        if (updateProductRequestDTO.getStock() != null) {
            if (updateProductRequestDTO.getStock() < 0) {
                throw new IllegalStateException("재고가 음수일 수 없습니다.");
            }
            existingProduct.setStock(updateProductRequestDTO.getStock());
        }

        if (updateProductRequestDTO.getSoldQuantity() != null) {
            if (updateProductRequestDTO.getSoldQuantity() < 0) {
                throw new IllegalStateException("판매량이 음수일 수 없습니다.");
            }
            existingProduct.setSoldQuantity(updateProductRequestDTO.getSoldQuantity());
        }

        if (updateProductRequestDTO.getName() != null) {
            existingProduct.setName(updateProductRequestDTO.getName());
            isNameModified = true;
        }

        if (updateProductRequestDTO.getBrand() != null) {
            existingProduct.setBrand(updateProductRequestDTO.getBrand());
            isBrandModified = true;
        }

        if (updateProductRequestDTO.getPrice() != null) {
            existingProduct.setPrice(updateProductRequestDTO.getPrice());
        }

        if (updateProductRequestDTO.getDiscountRate() != null) {
            existingProduct.setDiscountRate(updateProductRequestDTO.getDiscountRate());
        }

        if (updateProductRequestDTO.getDefectiveStock() != null) {
            existingProduct.setDefectiveStock(updateProductRequestDTO.getDefectiveStock());
        }

        if (updateProductRequestDTO.getDescription() != null) {
            existingProduct.setDescription(updateProductRequestDTO.getDescription());
        }

        if (updateProductRequestDTO.getImageUrl() != null) {
            existingProduct.setImageUrl(updateProductRequestDTO.getImageUrl());
        }

        if (updateProductRequestDTO.getCategory() != null) {
            existingProduct.setCategory(categoryRepository.findByCode(updateProductRequestDTO.getCategory()).orElseThrow(() -> new IdNotFoundException(updateProductRequestDTO.getCategory() + "(으)로 등록된 카테고리가 없습니다.")));
            isCategoryModified = true;
        }

        if (updateProductRequestDTO.getSize() != null && !updateProductRequestDTO.getSize().isEmpty()) {
            existingProduct.setSize(updateProductRequestDTO.getSize());
        }

        if (updateProductRequestDTO.getColor() != null && !updateProductRequestDTO.getColor().isEmpty()) {
            existingProduct.setColor(updateProductRequestDTO.getColor());
        }

        if (existingProduct.equals(beforeUpdate)) {
            throw new NoChangeException("변경된 상품 정보가 없습니다.");
        }

        if(isBrandModified || isNameModified || isCategoryModified){
            if (!newProductNum.equals(existingProduct.getProductNum())) {
                if (productRepository.existsByProductNum(newProductNum)) {
                    throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
                }
                existingProduct.setProductNum(newProductNum);
            }
        }

        Product product = productRepository.save(existingProduct);

        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);

        return converter.convertFromProductToProductResponse(product);
    }


    @Override
    @Transactional
    public String deleteById(Long productId) {
        Product product =findById(productId);
        String name = product.getName();
        productRepository.deleteById(productId);
        elasticsearchOperations.delete(String.valueOf(productId), ProductDocument.class);
        return name;
    }

    @Override
    @Transactional
    public ProductResponseForManager increaseStock(Long productId, Long stock) {
        if (stock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentStock = product.getStock();
        Long newStock = currentStock + stock;
        product.setStock(newStock);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }

    @Override
    @Transactional
    public ProductResponseForManager decreaseStock(Long productId, Long stock) {
        Product product = findById(productId);
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock - stock, 0);
        if (currentStock <= 0 || stock > currentStock) {
            throw new DataIntegrityViolationException("재고가 부족합니다.");
        }
        product.setStock(newStock);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }

    @Override
    @Transactional
    public ProductResponseForManager increaseSoldQuantity(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("증가시킬 판매 수량은 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentSoldQuantity = product.getSoldQuantity();
        Long newSoldQuantity = currentSoldQuantity + quantity;
        product.setSoldQuantity(newSoldQuantity);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }

    @Override
    @Transactional
    public ProductResponseForManager decreaseSoldQuantity(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("감소시킬 판매 수량은 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentSoldQuantity = product.getSoldQuantity();
        if (currentSoldQuantity < quantity) {
            throw new DataIntegrityViolationException("감소시킬 판매 수량이 현재 판매 수량보다 많습니다.");
        }
        Long newSoldQuantity = currentSoldQuantity - quantity;
        product.setSoldQuantity(newSoldQuantity);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }


    private String reCreateProductNum(String oldProductNum, UpdateProductRequestDTO updateProductRequestDTO){
        String frontOfOldProductNum = oldProductNum.substring(0,12);
        String middleOfNewProductNum = "" + updateProductRequestDTO.getBrand().charAt(0) + updateProductRequestDTO.getName().charAt(0);
        String newCategory = updateProductRequestDTO.getCategory();
        return frontOfOldProductNum+middleOfNewProductNum+newCategory;
    }

    @Override
    public Product findByProductOrderNum(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new IdNotFoundException(productOrderId + "(으)로 등록된 주문서가 없습니다."));
        return productOrder.getProduct();
    }

    @Override
    public Page<ProductSimpleResponseForManager> findAllByIdReturnProductResponseForManager(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Page<MemberProduct> memberProducts = memberProductRepository.findAllByMemberId(memberService.findByEmail(email).getId(), pageable);
        return converter.convertFromPagedMemberProductToPagedProductSimpleResponseForManager(memberProducts);
    }


    @Override
    public Page<ProductResponseForManager> findProductsOnElasticForAdmin(String brand, String category, String productName, String content, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        String categoryCode = null;
        if (category != null && !category.isEmpty()) {
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {
            categoryCode = getCode(content);
        }

        Page<ProductDocument> pagedDocuments = productElasticsearchRepository.findProducts(brand, categoryCode, productName, content, pageable);

        // 회원이 소유한 상품만 필터링하고 Page로 변환
        List<Product> filteredProducts = pagedDocuments
                .map(productDocument -> findById(productDocument.getId()))
                .getContent()
                .stream()
                .filter(product -> memberProductRepository.existsByMemberIdAndProductId(member.getId(), product.getId()))
                .collect(Collectors.toList());

        return converter.convertFromPagedProductToPagedProductResponseForManaging(
                new PageImpl<>(
                        filteredProducts,
                        pageable,
                        filteredProducts.size()
                )
        );
    }

    @Override
    @Transactional
    public ProductResponse updateMyProduct(UpdateProductRequestDTO updateProductRequestDTO) {

        if (confirmProductOwnership(updateProductRequestDTO.getName(), updateProductRequestDTO.getBrand())) {
            return update(updateProductRequestDTO);
        }

        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");

    }

    public boolean confirmProductOwnership(String productName, String brand) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Pageable pageable = pageUtil.pageable(PageRequest.of(1, 5));
        Page<MemberProduct> memberProducts = memberProductRepository
                .findAllByMemberId(memberService.findByEmail(email).getId(), pageable);

        return memberProducts.stream()
                .anyMatch(memberProduct ->
                        memberProduct.getProduct().getName().equals(productName) &&
                                memberProduct.getProduct().getBrand().equals(brand)
                );
    }

    @Override
    @Transactional
    public String deleteByIdForAdmin(Long productId) {
        Product product = findById(productId);
        String name = product.getName();
        if (confirmProductOwnership(name, product.getBrand())) {
            productRepository.deleteById(productId);
            elasticsearchOperations.delete(String.valueOf(productId), ProductDocument.class);
            return name;
        }
        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");
    }

    @Override
    @Transactional
    public ProductResponseForManager increaseStockForAdmin(Long productId, Long stock) {
        if (stock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        if (confirmProductOwnership(product.getName(), product.getBrand())) {
            Long currentStock = product.getStock();
            Long newStock = currentStock + stock;
            product.setStock(newStock);
            productRepository.save(product);
            ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
            indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
            return converter.convertFromProductToProductResponseForManaging(product);
        }
        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");

    }

    @Override
    @Transactional
    public ProductResponseForManager decreaseStockForAdmin(Long productId, Long stock) {
        Product product = findById(productId);
        if (confirmProductOwnership(product.getName(), product.getBrand())) {
            Long currentStock = product.getStock();
            Long newStock = Math.max(currentStock - stock, 0);
            if (currentStock <= 0 || stock > currentStock) {
                throw new DataIntegrityViolationException("재고가 부족합니다.");
            }
            product.setStock(newStock);
            productRepository.save(product);
            ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
            indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
            return converter.convertFromProductToProductResponseForManaging(product);
        }
        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");
    }

    @Override
    @Transactional
    public ProductResponseForManager increaseSoldQuantityForAdmin(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("증가시킬 판매 수량은 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        if (confirmProductOwnership(product.getName(), product.getBrand())) {
            Long currentSoldQuantity = product.getSoldQuantity();
            Long newSoldQuantity = currentSoldQuantity + quantity;
            product.setSoldQuantity(newSoldQuantity);
            productRepository.save(product);
            ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
            indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
            return converter.convertFromProductToProductResponseForManaging(product);
        }

        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");
    }

    @Override
    @Transactional
    public ProductResponseForManager decreaseSoldQuantityForAdmin(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("감소시킬 판매 수량은 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentSoldQuantity = product.getSoldQuantity();
        if (currentSoldQuantity < quantity) {
            throw new DataIntegrityViolationException("감소시킬 판매 수량이 현재 판매 수량보다 많습니다.");
        }
        if (confirmProductOwnership(product.getName(), product.getBrand())) {
            Long newSoldQuantity = currentSoldQuantity - quantity;
            product.setSoldQuantity(newSoldQuantity);
            productRepository.save(product);
            ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
            indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
            return converter.convertFromProductToProductResponseForManaging(product);
        }

        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");

    }



    @Override
    public Product findByProductOrderNumForAdmin(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new IdNotFoundException(productOrderId + "(으)로 등록된 주문서가 없습니다."));

        if (confirmProductOwnership(productOrder.getProduct().getName(), productOrder.getProduct().getBrand())) {
            return productOrder.getProduct();
        }

        throw new IllegalArgumentException("귀사의 상품이 맞는지 확인해주세요.");

    }

}