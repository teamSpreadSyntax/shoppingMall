package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.WishList;
import home.project.dto.requestDTO.AddWishRequestDTO;
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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductService productService;
    private final MemberService memberService;
    private final Converter converter;

    @Override
    public WishListResponse toggleWishList(Long productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Product product = productService.findById(productId);

        WishList existingWishList = wishListRepository.findByMemberIdAndProductId(member.getId(), product.getId());
        if (existingWishList != null) {
            wishListRepository.delete(existingWishList);
            return new WishListResponse(existingWishList.getId(), product.getId(), "위시 리스트에서 삭제 되었습니다");
        } else {
            WishList newWishList = new WishList();
            newWishList.setMember(member);
            newWishList.setProduct(product);
            newWishList.setCreateAt(LocalDateTime.now());
            wishListRepository.save(newWishList);
            return new WishListResponse(newWishList.getId(), product.getId(), "위시리스트에 저장되었습니다");
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
}
