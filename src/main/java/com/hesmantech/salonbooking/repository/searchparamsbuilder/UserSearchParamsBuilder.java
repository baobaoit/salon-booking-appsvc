package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.api.dto.user.SearchUserRequest;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.mapper.base.PhoneNumberMapper;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QUserEntity.userEntity;

public class UserSearchParamsBuilder extends AbstractSearchParamsBuilder {
    private final UserSortProperty sortProperty;
    private final List<UserRole> userRoles;
    private final String name;
    private final String phoneNumber;
    private final List<UserStatus> statuses;

    private UserSearchParamsBuilder(Builder builder) {
        super(builder);
        this.sortProperty = builder.sortProperty;
        this.userRoles = builder.userRoles;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.statuses = builder.statuses;
    }

    public static UserSearchParamsBuilder from(int page, int size, Sort.Direction direction,
                                               UserSortProperty sortProperty, SearchUserRequest searchUserRequest) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withSortProperty(sortProperty)
                .withUserRoles(searchUserRequest.userRoles())
                .withName(searchUserRequest.name())
                .withPhoneNumber(PhoneNumberMapper.asStandardized(searchUserRequest.phoneNumber()))
                .withStatuses(searchUserRequest.statuses())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        return Stream.of(
                        Optional.ofNullable(userRoles).map(roles -> roles.stream()
                                        .map(UserRole::id)
                                        .toList())
                                .filter(CollectionUtils::isNotEmpty)
                                .map(userEntity.role.id::in),
                        Optional.ofNullable(phoneNumber).filter(StringUtils::isNotBlank)
                                .map(this::wrapInPercentSymbols)
                                .map(userEntity.phoneNumber::likeIgnoreCase),
                        Optional.ofNullable(statuses).map(sttList -> sttList.isEmpty() ?
                                userEntity.status.ne(UserStatus.DELETED) :
                                userEntity.status.in(sttList)),
                        Optional.ofNullable(name).filter(StringUtils::isNotBlank)
                                .map(this::wrapInPercentSymbols)
                                .map(userEntity.firstName
                                        .concat(" ")
                                        .concat(userEntity.lastName)::likeIgnoreCase))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::and);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(sortProperty.getProperty());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private UserSortProperty sortProperty = UserSortProperty.CREATED_DATE;
        private List<UserRole> userRoles;
        private String name;
        private String phoneNumber;
        private List<UserStatus> statuses;

        public Builder withSortProperty(UserSortProperty sortProperty) {
            this.sortProperty = sortProperty;
            return this;
        }

        public Builder withUserRoles(List<UserRole> userRoles) {
            this.userRoles = userRoles;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withStatuses(List<UserStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public UserSearchParamsBuilder build() {
            return new UserSearchParamsBuilder(this);
        }
    }
}
