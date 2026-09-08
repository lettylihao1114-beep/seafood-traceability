package com.seafood.common;

import com.seafood.dto.StatItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MapConvert {

    private MapConvert() {
    }

    public static List<StatItem> toStat(List<Map<String, Object>> rows) {
        return rows.stream().map(row -> new StatItem(
                String.valueOf(row.get("name")),
                ((Number) row.get("value")).longValue()
        )).collect(Collectors.toList());
    }
}
