package net.craftingstore;

public class Package {

    private int id;
    private String name;
    private Integer category;
    private String minecraftIconName;
    private String ingameDescription;
    private String url;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCategory() {
        return category;
    }

    public String getIngameDescription() {
        return ingameDescription;
    }

    public String getMinecraftIconName() {
        return minecraftIconName;
    }

    public String getUrl() {
        return url;
    }
}