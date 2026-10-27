package com.affiliateLink.profile.mapper;
import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.entity.Advertiser;
import org.mapstruct.Mapper;
import java.util.List;
@Mapper(componentModel = "spring")
public interface AdvertiserMapper {
    Advertiser toEntity(CreateAdvertiserRequest request);
    AdvertiserResponse toResponse(Advertiser entity);
    List<AdvertiserResponse> toResponses(List<Advertiser> entities);
}