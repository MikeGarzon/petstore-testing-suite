package org.petstore.pojo.request;

import java.util.List;
import lombok.Data;
import org.petstore.pojo.common.Tag;
import org.petstore.pojo.common.Category;

@Data
public class PetRequest {
    private String name;
    private Category category;
    private List<Tag> tags;
    private String status;
}

