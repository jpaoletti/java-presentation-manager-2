package jpaoletti.jpm2.core.model;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Tags for entities. Abstract class that must be implemented with a reference
 * to de tagged class.
 *
 * CREATE TABLE xxxxx_tags ( id BIGINT auto_increment NOT NULL, description
 * varchar(1000) NULL, style varchar(100) NULL, operations varchar(1000) NULL,
 * CONSTRAINT xxxxx_tags_PK PRIMARY KEY (id) ) ENGINE=InnoDB DEFAULT
 * CHARSET=utf8 COLLATE=utf8_general_ci;
 *
 * CREATE TABLE `join_xxxxx_tags` ( `reference` bigint NOT NULL, `tag` bigint
 * NOT NULL, PRIMARY KEY (`tag`,`reference`), KEY
 * `join_xxxxx_tags_reference_IDX` (`reference`) USING BTREE, KEY
 * `join_xxxxx_tags_tag_IDX` (`tag`) USING BTREE, CONSTRAINT
 * `join_xxxxx_tags_FK` FOREIGN KEY (`reference`) REFERENCES `xxxxx`
 * (`xxxxx_id`) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT
 * `join_xxxxx_tags_FK_1` FOREIGN KEY (`tag`) REFERENCES `xxxxx_tags` (`id`) ON
 * DELETE CASCADE ON UPDATE CASCADE ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 *
 * @author jpaoletti
 */
@MappedSuperclass
public abstract class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    private String style;

    private String operations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        final Tag other = (Tag) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        }
        return getDescription();
    }

}
