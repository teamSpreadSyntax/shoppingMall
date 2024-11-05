//package home.project.service;
//
//import home.project.domain.Member;
//import home.project.domain.Product;
//import home.project.domain.Seller;
//import home.project.dto.requestDTO.CreateSellerRequestDTO;
//import home.project.dto.responseDTO.ProductSellerResponse;
//import home.project.dto.responseDTO.SellerResponse;
//import home.project.exceptions.exception.IdNotFoundException;
//import home.project.repository.ProductRepository;
//import home.project.repository.SellerRepository;
//import home.project.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@RequiredArgsConstructor
//@Service
//@Transactional(readOnly = true)
//public class SellerServiceImpl implements SellerService {
//
//    private final SellerRepository sellerRepository;
//    private final ProductRepository productRepository;
//    private final MemberService memberService;
//    private final Converter converter;
//
//    @Transactional
//    @Override
//    public SellerResponse createSeller(CreateSellerRequestDTO requestDTO) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        Member member = memberService.findByEmail(email);
//
//        Seller seller = new Seller();
//        seller.setName(requestDTO.getName());
//        seller.setPhoneNumber(requestDTO.getPhoneNumber());
//        seller.setEmail(requestDTO.getEmail());
//        seller.setAddress(requestDTO.getAddress());
//        seller.setMember(member);
//
//        Seller savedSeller = sellerRepository.save(seller);
//        return converter.convertFromSellerToSellerResponse(savedSeller);
//    }
//
//    @Override
//    public Page<ProductSellerResponse> findProductsBySeller(Long sellerId, Pageable pageable) {
//        Seller seller = sellerRepository.findById(sellerId)
//                .orElseThrow(() -> new IdNotFoundException(sellerId + "번 판매자를 찾을 수 없습니다."));
//
//        Page<Product> products = productRepository.findBySeller(seller, pageable);
//        return products.map(converter::convertFromProductToProductSellerResponse);
//    }
//}
