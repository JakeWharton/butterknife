package butterknife;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/** An binder will apply views property when called. */
public interface Binder {
    @UiThread
    <Target,Data>void apply(Target target,Data data);
    void apply(TextView target, CharSequence data);
    void apply(Button target, CharSequence data);
    void apply(TextView target, @StringRes int data);
    void apply(Button target, @StringRes int data);

    /**
     * use setImageUri on target
     * @param target
     * @param data
     */
    void apply(ImageView target, CharSequence data);
    void apply(ImageView target, @DrawableRes int data);

    /**
     *
     */
    class DefaultBinder implements Binder{
        @Override
        public <Target, Data> void apply(Target bean, Data data) {

        }

        @Override
        public void apply(TextView target, CharSequence data) {
            target.setText(data);
        }

        @Override
        public void apply(Button target, CharSequence data) {
            target.setText(data);
        }

        @Override
        public void apply(TextView target, @StringRes int data) {
            target.setText(data);
        }

        @Override
        public void apply(Button target, @StringRes int data) {
            target.setText(data);
        }

        @Override
        public void apply(ImageView target, CharSequence data) {
            target.setImageURI(Uri.fromFile(new File(data.toString())));
        }

        @Override
        public void apply(ImageView target, @DrawableRes int data) {
            target.setImageResource(data);
        }
    }
}
