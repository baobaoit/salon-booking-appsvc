package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.customercredit.CustomerCreditDetails;
import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditResponse;
import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.mapper.base.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreditMapper extends BigDecimalMapper {
    CreditMapper INSTANCE = Mappers.getMapper(CreditMapper.class);

    @Named("extractCustomerName")
    static String extractCustomerName(UserEntity customer) {
        return customer.getFirstName() + " " + customer.getLastName();
    }

    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "customer", target = "customerName", qualifiedByName = "extractCustomerName")
    @Mapping(source = "availableCredit", target = "availableCredit", qualifiedByName = "toBigDecimal")
    @Mapping(source = "totalCredit", target = "totalCredit", qualifiedByName = "toBigDecimal")
    SearchCustomerCreditResponse toSearchCustomerCreditResponse(CreditEntity creditEntity);

    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "customer", target = "customerName", qualifiedByName = "extractCustomerName")
    @Mapping(source = "availableCredit", target = "availableCredit", qualifiedByName = "toBigDecimal")
    @Mapping(source = "totalCredit", target = "totalCredit", qualifiedByName = "toBigDecimal")
    CustomerCreditDetails toCustomerCreditDetails(CreditEntity creditEntity);
}
