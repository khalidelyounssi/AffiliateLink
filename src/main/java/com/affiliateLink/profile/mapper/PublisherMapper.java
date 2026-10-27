package com.affiliateLink.profile.mapper;
import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.entity.Publisher;
import org.mapstruct.Mapper;
import java.util.List;
@Mapper(componentModel = "spring")
public interface PublisherMapper {
    Publisher toEntity(CreatePublisherRequest request);
    PublisherResponse toResponse(Publisher entity);
    List<PublisherResponse> toResponses(List<Publisher> entities);
}