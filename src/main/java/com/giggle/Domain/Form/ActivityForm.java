package com.giggle.Domain.Form;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ActivityForm {
    List resultList;
    int totalCnt;

    public ActivityForm(List list, int cnt){
        this.resultList = list;
        this.totalCnt = cnt;
    }
}
