package com.topaiebiz.elasticsearch.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.topaiebiz.elasticsearch.dto.ItemDto;

import java.io.IOException;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 19:54 2018/6/21
 * @Modified by:
 */
public class ItemDtoSerialize extends JsonSerializer<ItemDto> {
	@Override
	public void serialize(ItemDto itemDto, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeNumberField("id", itemDto.getId());
		gen.writeStringField("name", itemDto.getName());
		gen.writeNumberField("marketPrice", itemDto.getMarketPrice());
		gen.writeNumberField("defaultPrice", itemDto.getDefaultPrice());
		gen.writeNumberField("belongStore", itemDto.getBelongStore());
		gen.writeStringField("brandName", itemDto.getBrandName());
		gen.writeStringField("backName", itemDto.getBackName());
		gen.writeNumberField("salesVolume", itemDto.getSalesVolume());
		gen.writeStringField("pictureName", itemDto.getPictureName());
		gen.writeNumberField("deletedFlag", itemDto.getDeletedFlag());
		gen.writeNumberField("status", itemDto.getStatus());
		gen.writeNumberField("frozenFlag", itemDto.getFrozenFlag());
		gen.writeEndObject();
	}
}
