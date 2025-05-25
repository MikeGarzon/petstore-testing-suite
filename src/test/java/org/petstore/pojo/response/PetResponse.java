package org.petstore.pojo.response;

import lombok.Data;
import java.util.List;
import org.petstore.pojo.common.Tag;
import org.petstore.pojo.common.Category;

@Data
public class PetResponse {
    private Long id;
    private String name;
    private Category category;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;
}

