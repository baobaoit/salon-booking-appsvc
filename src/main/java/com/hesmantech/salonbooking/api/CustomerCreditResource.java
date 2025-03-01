package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.customercredit.CustomerCreditDetails;
import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditRequest;
import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditResponse;
import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Customer Credit Resource")
@Validated
public interface CustomerCreditResource {
    @Operation(summary = "Explore Customer Credit")
    PageResponse<SearchCustomerCreditResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                                      @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
                                                      @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                                      @RequestParam(required = false, defaultValue = "CREATED_DATE") CustomerCreditSortProperty property,
                                                      @RequestBody SearchCustomerCreditRequest request,
                                                      Principal principal);

    @Operation(summary = "Obtain the credit information of a customer")
    CustomerCreditDetails getDetails(UUID customerId, Principal principal);
}
