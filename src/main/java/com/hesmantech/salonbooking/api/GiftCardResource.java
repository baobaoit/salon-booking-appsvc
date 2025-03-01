package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.CreateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.GiftCardCode;
import com.hesmantech.salonbooking.api.dto.giftcard.GiftCardResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.UpdateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Gift Card Resource")
@Validated
public interface GiftCardResource {
    @Operation(summary = "Craft a New Gift Card")
    GiftCardResponse create(@Valid @RequestBody CreateGiftCardRequest createGiftCardRequest, Principal principal);

    @Operation(summary = "Retrieve Details of a Chosen Gift Card")
    GiftCardResponse getDetails(UUID id, Principal principal);

    @Operation(summary = "Revise a Designated Gift Card")
    GiftCardResponse update(UUID id, @Valid @RequestBody UpdateGiftCardRequest updateGiftCardRequest, Principal principal);

    @Operation(summary = "Produce a Unique and Secure Gift Code")
    GiftCardCode generateGiftCardCode(Principal principal);

    @Operation(summary = "Explore Gift Cards")
    PageResponse<SearchGiftCardResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                                @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
                                                @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                                @RequestParam(required = false, defaultValue = "DATE_ISSUED") GiftCardSortProperty property,
                                                @RequestBody SearchGiftCardRequest request, Principal principal);

    @Operation(summary = "Deactivate a Selected Gift Card")
    GiftCardResponse deactivate(UUID id, Principal principal);

    @Operation(summary = "Customer to Claim Their Gift Card")
    SearchGiftCardResponse redeem(UUID id, UUID customerId, Principal principal);

    @Operation(summary = "Disassociate a customer from a gift card")
    boolean unlink(UUID id, UUID customerId, Principal principal);
}
