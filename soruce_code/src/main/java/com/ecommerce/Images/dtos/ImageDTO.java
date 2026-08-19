package com.ecommerce.Images.dtos;

import com.ecommerce.Images.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Long id;
    private String url;



    static public ImageDTO fromImage(Image image){
        return new ImageDTO(image.getId(),image.getImageUrl());
    }
}
