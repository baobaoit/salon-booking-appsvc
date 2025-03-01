package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.user.CustomerGiftCardResponse;
import com.hesmantech.salonbooking.api.dto.user.NailTechnicianResponse;
import com.hesmantech.salonbooking.api.dto.user.UserResponse;
import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.mapper.base.PhoneNumberMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper extends PhoneNumberMapper, InstantMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "role.name", target = "role")
    @Mapping(source = "phoneNumber", target = "phoneNumber", qualifiedByName = "phoneNumberToUSFormat")
    @Mapping(source = "dob", target = "dob", qualifiedByName = "instantToString")
    UserResponse toUserResponse(UserEntity user);

    NailTechnicianResponse toNailTechnicianResponse(UserEntity user);

    @Mapping(source = "employeeId", target = "id")
    @Mapping(source = "employeeFirstName", target = "firstName")
    @Mapping(source = "employeeLastName", target = "lastName")
    @Mapping(source = "employeePhoneNumber", target = "phoneNumber")
    NailTechnicianResponse toNailTechnicianResponse(OrderedDetailsEntity orderedDetails);

    default NailTechnicianResponse toNailTechnicianResponse(OrderEntity order) {
        OrderStatus orderStatus = order.getStatus();
        boolean isOrderCheckedOut = OrderStatus.CHECK_OUT.equals(orderStatus);

        return isOrderCheckedOut ?
                toNailTechnicianResponse(order.getOrderedDetails().get(0)) :
                toNailTechnicianResponse(order.getEmployee());
    }

    @Mapping(source = "redeemed", target = "hasRedeemed")
    CustomerGiftCardResponse toCustomerGiftCardResponse(CustomerGiftCardEntity customerGiftCard);

    default Set<CustomerGiftCardResponse> toCustomerGiftCardResponseSet(Set<CustomerGiftCardEntity> customers) {
        return customers == null ? Set.of() :
                customers.stream()
                        .map(this::toCustomerGiftCardResponse)
                        .collect(Collectors.toSet());
    }
}
