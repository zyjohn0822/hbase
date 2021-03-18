package com.hisense.hiatmp.hbase.api.internal;

import com.hisense.hiatmp.hbase.api.RowKeyStrategy;

/**
 * <p>class TimeMD5IDStrategy</p>
 * <p>@description 基于时间和MD5(ID)的row-key策略</p>
 * <p>@author john</p>
 * <p>@create 2021/2/8</p>
 * <p>@version</p>
 */
public class TimeMD5IDStrategy implements RowKeyStrategy {

    @Override
    public byte[] generate(String id) {
        return new byte[0];
    }
}
