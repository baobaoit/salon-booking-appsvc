package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.customer.CustomerResponse;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.mapper.base.PhoneNumberMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper extends PhoneNumberMapper, InstantMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "dob", target = "dob", qualifiedByName = "instantToString")
    @Mapping(source = "phoneNumber", target = "phoneNumber", qualifiedByName = "phoneNumberToUSFormat")
    CustomerResponse toCustomerResponse(UserEntity user);
}
