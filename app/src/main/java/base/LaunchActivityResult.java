package base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({
        LaunchActivityResult.noData,
        LaunchActivityResult.permissions,
})
@Retention(RetentionPolicy.SOURCE)
public @interface LaunchActivityResult {
    int noData = 0;
    int permissions = 1;

}
