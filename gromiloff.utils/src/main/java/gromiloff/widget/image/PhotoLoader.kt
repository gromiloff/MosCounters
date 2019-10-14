package gromiloff.widget.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.AnyThread
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import gromiloff.utils.R
import gromiloff.widget.image.transformation.CropSquareTransformation
import gromiloff.widget.image.transformation.RoundedCornersTransformation
import java.util.*


open class PhotoLoader : ImageView, RequestListener<Drawable> {
    val listTransformations = ArrayList<BitmapTransformation>()

    private var placeHolder: Drawable? = null
    private var isSquare = false
    var currentPath: String? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec

        if (this.isSquare) {
            val width = View.MeasureSpec.getSize(widthMeasureSpec)
            val height = View.MeasureSpec.getSize(heightMeasureSpec)

            val selected: Int
            if (width != 0 && height != 0) {
                if (width > height) {
                    selected = height
                    widthSpec = heightMeasureSpec
                } else {
                    selected = width
                    heightSpec = widthMeasureSpec
                }
            } else if (width == 0) {
                selected = height
                widthSpec = heightMeasureSpec
            } else {
                selected = width
                heightSpec = widthMeasureSpec
            }
            if (selected > 0) {
                setMeasuredDimension(selected, selected)
            }
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean) = false

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean) = false

    @SuppressLint("CheckResult")
    @AnyThread
    open fun show(path: String?) {
        try {
            val opt = if (this.listTransformations.isNotEmpty()) {
                RequestOptions().transforms(*this.listTransformations.toTypedArray())
            } else {
                RequestOptions()
            }
            val a = GlideApp.with(this)
                    .load(path)
                    .apply(opt)
                    .error(this.placeHolder)
                    .listener(this)
            if (Looper.getMainLooper().thread == Thread.currentThread()) {
                a.into(this)
            } else {
                post { a.into(this) }
            }
            this.currentPath = path
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun reset() {
        GlideApp.with(this).clear(this)
    }

    fun applyBaseTransformation(value: Int) {
        when (value) {
            Square -> {
                this.isSquare = true
                this.listTransformations.add(CropSquareTransformation())
            }
            crop_center -> this.listTransformations.add(CenterCrop())
            Round -> this.listTransformations.add(RoundedCornersTransformation(
                    resources.getDimensionPixelOffset(R.dimen.gromiloff_widget_image_corner),
                    RoundedCornersTransformation.CornerType.ALL))
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PhotoViewWrapper, 0, 0)
        if (a != null) {
            if (a.hasValue(R.styleable.PhotoViewWrapper_errorHolder)) {
                this.placeHolder = ContextCompat.getDrawable(context, a.getResourceId(R.styleable.PhotoViewWrapper_errorHolder, -1))
            }

            if (a.hasValue(R.styleable.PhotoViewWrapper_transformation_type)) {
                val transformationTypes = a.getInt(R.styleable.PhotoViewWrapper_transformation_type, 0)
                if (transformationTypes > 0) {
                    if (containsFlag(transformationTypes, Square)) {
                        applyBaseTransformation(Square)
                    }
                    if (containsFlag(transformationTypes, crop_center)) {
                        applyBaseTransformation(crop_center)
                    }
                    if (containsFlag(transformationTypes, Round)) {
                        applyBaseTransformation(Round)
                    }
                }
            }
            a.recycle()
        }
    }

    private fun containsFlag(src: Int, flag: Int) = src or flag == src

    companion object {
        const val Square = 1
        // полное скругление
        const val Round = 2
        const val crop_center = 4
    }
}