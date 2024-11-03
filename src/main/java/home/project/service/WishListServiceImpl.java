package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.WishList;
import home.project.dto.responseDTO.WishListDetailResponse;
import home.project.dto.responseDTO.WishListResponse;
import home.project.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    public WishListResponse toggleWishList(Long productId, boolean liked) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Product product = productService.findById(productId);

        WishList existingWishList = wishListRepository.findByMemberIdAndProductId(member.getId(), product.getId());
        if (existingWishList != null) {

            existingWishList.setLiked(liked);
            wishListRepository.save(existingWishList);

            String message = liked ? "좋아요가 설정되었습니다." : "좋아요가 해제되었습니다.";
            return new WishListResponse(existingWishList.getId(), product.getId(), liked, message);
        } else {
            WishList newWishList = new WishList();
            newWishList.setMember(member);
            newWishList.setProduct(product);
            newWishList.setCreateAt(LocalDateTime.now());
            newWishList.setLiked(liked);
            wishListRepository.save(newWishList);

            String message = "위시리스트에 저장되었습니다";
            return new WishListResponse(newWishList.getId(), product.getId(), liked, message);
        }
    }

    @Override
    public Page<WishListDetailResponse> findAllMyWishList(Pageable pageable){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Page<WishList> pagedWishList = wishListRepository.findAllByMemberId(member.getId(), pageable);
        return converter.convertFromPagedWishListToPagedWishListResponse(pagedWishList);
    }

    public List<WishList> findByMemberId(Long memberId) {
        return wishListRepository.findByMemberId(memberId);
    }
}
