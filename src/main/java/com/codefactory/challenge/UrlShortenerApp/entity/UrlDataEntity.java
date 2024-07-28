package com.codefactory.challenge.UrlShortenerApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "URL_DATA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableGenerator(name="url_data_seq", initialValue=250000, allocationSize=50)
public class UrlDataEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="url_data_seq")
    private Integer id;

    private String longUrl;
}
