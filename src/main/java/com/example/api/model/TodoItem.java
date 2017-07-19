package com.example.api.model;

import com.fasterxml.jackson.annotation.*;

/**
 * Created by yaweiw on 7/19/2017.
 */
@JsonPropertyOrder({ "ID", "Description", "Owner" })
public class TodoItem
{
    @JsonRawValue
    private String Description;
    @JsonRawValue
    private int ID;
    @JsonRawValue
    private String Owner;
    @JsonCreator
    public TodoItem(
            @JsonProperty("ID") int id,
            @JsonProperty("Description") String description,
            @JsonProperty("Owner") String owner
    ) {
        this.Description = description;
        this.ID = id;
        this.Owner = null;
    }

    /*public TodoItem(String description, int id, String owner) {
        this.Description = description;
        this.ID = id;
        this.Owner = owner;
    }*/

    @JsonGetter("Description")
    public String getDescription() {
        return Description;
    }

    @JsonSetter("Description")
    public void setDescription(String description) {
        this.Description = description;
    }

    @JsonGetter("Owner")
    public String getOwner() {
        return Owner;
    }

    @JsonSetter("Owner")
    public void setOwner(String owner) {
        this.Owner = owner;
    }

    @JsonGetter("ID")
    public int getID() {
        return ID;
    }

    @JsonSetter("ID")
    public void setID(int id) {
        this.ID = id;
    }
}
