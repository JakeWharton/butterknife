/**
 * View "injection" library for Android which uses annotation processing to generate boilerplate
 * code for you.
 * <p>
 * <ul>
 * <li>Eliminate {@link android.view.View#findViewById findViewById} calls by using
 * {@link butterknife.InjectView @InjectView} on fields.</li>
 * <li>Group multiple views in a {@linkplain java.util.List list} using
 * {@link butterknife.InjectViews @InjectViews}. Operate on all of them at once with
 * {@linkplain butterknife.ButterKnife#apply(java.util.List, butterknife.ButterKnife.Action)
 * actions}, {@linkplain butterknife.ButterKnife#apply(java.util.List,
 * butterknife.ButterKnife.Setter, Object) setters}, or
 * {@linkplain butterknife.ButterKnife#apply(java.util.List, android.util.Property, Object)
 * properties}.</li>
 * <li>Eliminate anonymous inner-classes for listeners by annotating methods with
 * {@link butterknife.OnClick @OnClick} and others.</li>
 * </ul>
 */
package butterknife;
