package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.LatestGCBResponse;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.RedeemByCodeRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityResponse;
import com.hesmantech.salonbooking.api.dto.sort.giftcardbalance.GCBActivitySortProperty;
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

@Tag(name = "Gift Card Balance Resource")
@Validated
public interface GiftCardBalanceResource {
    @Operation(summary = "Retrieve the most recent balance of a customer's gift card")
    LatestGCBResponse retrieveGiftCardBalance(UUID customerId, Principal principal);

    @Operation(summary = "Apply customer gift code towards their gift card balance")
    LatestGCBResponse redeemGiftCardCode(UUID customerId, @Valid @RequestBody RedeemByCodeRequest request, Principal principal);

    @Operation(summary = "Check gift card balance activity")
    PageResponse<SearchGCBActivityResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                                   @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
                                                   @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                                   @RequestParam(required = false, defaultValue = "CREATED_DATE") GCBActivitySortProperty property,
                                                   @RequestBody SearchGCBActivityRequest request,
                                                   Principal principal);
}
