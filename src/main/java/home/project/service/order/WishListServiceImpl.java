package home.project.service.order;

import home.project.domain.common.WishList;
import home.project.domain.member.Member;
import home.project.domain.product.Product;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.WishListResponse;
import home.project.repository.product.WishListRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductService productService;
    private final MemberService memberService;
    private final Converter converter;

    @Transactional
    @Override
    public WishListResponse addToWishList(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);
        Product product = productService.findById(productId);

        // 이미 위시리스트에 있는지 확인
        WishList existingWishList = wishListRepository.findByMemberIdAndProductId(member.getId(), product.getId());
        if (existingWishList != null) {
            return new WishListResponse(existingWishList.getId(), product.getId(), true, "이미 위시리스트에 존재합니다.");
        }

        // 새로운 위시리스트 항목 생성
        WishList newWishList = new WishList();
        newWishList.setMember(member);
        newWishList.setProduct(product);
        newWishList.setCreateAt(LocalDateTime.now());
        newWishList.setLiked(true);
        wishListRepository.save(newWishList);

        return new WishListResponse(newWishList.getId(), product.getId(), true, "위시리스트에 추가되었습니다.");
    }

    @Transactional
    @Override
    public WishListResponse removeFromWishList(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);
        Product product = productService.findById(productId);

        WishList wishList = wishListRepository.findByMemberIdAndProductId(member.getId(), product.getId());
        if (wishList == null) {
            return new WishListResponse(null, productId, false, "위시리스트에 존재하지 않습니다.");
        }

        wishListRepository.delete(wishList);
        return new WishListResponse(wishList.getId(), productId, false, "위시리스트에서 삭제되었습니다.");
    }
    /*public WishListResponse toggleWishList(Long productId, boolean liked) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Product product = productService.findById(productId);

        WishList existingWishList = wishListRepository.findByMemberIdAndProductId(member.getId(), product.getId());
        if (existingWishList != null) {
            if (!liked) {
                wishListRepository.delete(existingWishList);
                return new WishListResponse(existingWishList.getId(), product.getId(), false, "위시리스트에서 제거되었습니다");
            } else {
                existingWishList.setLiked(true);
                wishListRepository.save(existingWishList);
                return new WishListResponse(existingWishList.getId(), product.getId(), true, "좋아요가 설정되었습니다.");
            }
        } else {
            if (liked) {
                WishList newWishList = new WishList();
                newWishList.setMember(member);
                newWishList.setProduct(product);
                newWishList.setCreateAt(LocalDateTime.now());
                newWishList.setLiked(true);
                wishListRepository.save(newWishList);

                return new WishListResponse(newWishList.getId(), product.getId(), true, "위시리스트에 저장되었습니다");
            } else {
                // 좋아요가 설정되지 않은 상태로는 위시리스트에 추가하지 않음
                return new WishListResponse(null, product.getId(), false, "좋아요가 설정되지 않았습니다.");
            }
        }
    }*/

    @Override
    public Page<ProductResponse> findAllMyWishList(Pageable pageable){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Page<WishList> pagedWishList = wishListRepository.findAllByMemberId(member.getId(), pageable);
        return converter.convertFromPagedWishListToProductResponseResponse(pagedWishList);
    }

}
