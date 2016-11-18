package jp.tenposs.listener;

import java.io.Serializable;

/**
 * Created by ambient on 11/18/16.
 */

public interface BSSelectionListener {
    void onItemSelect(int position, Serializable extras);

    void onCancel();
}