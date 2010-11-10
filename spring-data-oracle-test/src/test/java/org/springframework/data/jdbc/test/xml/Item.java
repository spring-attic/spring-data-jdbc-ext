package org.springframework.data.jdbc.test.xml;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlRootElement(name = "item")
@XmlType(propOrder = {"name", "price"})
public class Item{
    private Long id = 0L;
    private String name;
    private BigDecimal price;

    @XmlAttribute(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "item-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public String toString() {
        return "[" + id + "] " + name + " " + price;
    }
}
