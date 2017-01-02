package com.jtouzy.fastrecord.tests.metadata.workingEntities;

import com.jtouzy.fastrecord.annotations.Entity;
import com.jtouzy.fastrecord.annotations.Id;

@Entity
public class Event {
    @Id
    private Integer id;
    private String title;
    private Category category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
