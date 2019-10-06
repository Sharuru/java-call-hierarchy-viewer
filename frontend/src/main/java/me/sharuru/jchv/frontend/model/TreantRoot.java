package me.sharuru.jchv.frontend.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class TreantRoot {
    private TreantNode text;
    List<TreantRoot> children = new ArrayList<>();
}

