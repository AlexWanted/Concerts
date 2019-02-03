package ru.rewindforce.concerts.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.jetbrains.anko.support.v4.ctx
import ru.rewindforce.concerts.AddConcert.AddConcertPresenter.getImageActivityIntent
import ru.rewindforce.concerts.BitmapHelper
import ru.rewindforce.concerts.authorization.SignUpFragment.enableInputError
import java.io.FileNotFoundException
import java.util.regex.Pattern

private const val BUNDLE_FIRST_NAME = "bundle_first_name"
private const val BUNDLE_LAST_NAME = "bundle_last_name"
private const val BUNDLE_AVATAR_PATH = "bundle_avatar_path"
private const val BUNDLE_HEADER_PATH = "bundle_header_path"

private const val GET_AVATAR_RESPONSE = 1
private const val GET_HEADER_RESPONSE = 2

class EditProfileFragment: BaseFragment(R.layout.fragment_edit_profile) {

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var avatarPath: String
    private lateinit var headerPath: String

    private val presenter: EditProfilePresenter by lazy { EditProfilePresenter() }

    private var isFirstNameValid: Boolean = true
    private var isLastNameValid: Boolean = true
    private var avatarByteArray: ByteArray? = null
    private var headerByteArray: ByteArray? = null

    private val pattern: Pattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+")
    private val errorMsg = "Имя может содержать только латинские (A-z) и кириллические (А-я) буквы"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            firstName = it.getString(BUNDLE_FIRST_NAME) ?: ""
            lastName = it.getString(BUNDLE_LAST_NAME) ?: ""
            avatarPath = it.getString(BUNDLE_AVATAR_PATH) ?: ""
            headerPath = it.getString(BUNDLE_HEADER_PATH) ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editFirstName.setText(firstName)
        editLastName.setText(lastName)

        editFirstName.addTextChangedListener(getTextWatcher ( onChanged = {s: CharSequence ->
            if (pattern.matcher(s).matches()) {
                inputFirstName.isErrorEnabled = false
                isFirstNameValid = true
            } else {
                enableInputError(inputFirstName, errorMsg)
                isFirstNameValid = false
            }
        }))

        editLastName.addTextChangedListener(getTextWatcher ( onChanged = {s: CharSequence ->
            if (pattern.matcher(s).matches()) {
                inputLastName.isErrorEnabled = false
                isLastNameValid = true
            } else {
                enableInputError(inputLastName, errorMsg)
                isLastNameValid = false
            }
        }))

        Glide.with(ctx).load(avatarPath).thumbnail(0.1f).apply(RequestOptions().circleCrop()).into(avatar)
        Glide.with(ctx).load(headerPath).thumbnail(0.1f).into(header)

        avatar.setOnClickListener { startActivityForResult(getImageActivityIntent(), GET_AVATAR_RESPONSE) }
        header.setOnClickListener { startActivityForResult(getImageActivityIntent(), GET_HEADER_RESPONSE) }
        buttonAccept.setOnClickListener { attemptToAccept() }
        returnButton.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.attach(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.data?.let { when (requestCode) {
                GET_AVATAR_RESPONSE -> try {
                    val inputStream = ctx.contentResolver.openInputStream(it)
                    val currentAvatarBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    avatarByteArray = BitmapHelper.getCompressedBitmapData(currentAvatarBitmap, 1000000, 1000)

                    Glide.with(ctx).load(currentAvatarBitmap).thumbnail(0.1f).apply(RequestOptions().circleCrop()).into(avatar)
                } catch (e: FileNotFoundException) { e.printStackTrace() }
                GET_HEADER_RESPONSE -> try {
                    val inputStream = ctx.contentResolver.openInputStream(it)
                    val currentHeaderBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    avatarByteArray = BitmapHelper.getCompressedBitmapData(currentHeaderBitmap, 1000000, 1000)

                    Glide.with(ctx).load(currentHeaderBitmap).thumbnail(0.1f).into(header)
                } catch (e: FileNotFoundException) { e.printStackTrace() }
                else -> { }
            }
        }
    }

    private fun attemptToAccept() {
        val firstName = editFirstName.text.toString()
        val lastName = editLastName.text.toString()

        if (isFirstNameValid && isLastNameValid) {
            buttonAccept.startAnimation()
            presenter.editProfileInfo(firstName, lastName, avatarByteArray, headerByteArray)
        }
    }

    override fun onSuccess() {
        val color = ContextCompat.getColor(ctx, R.color.done)
        val icon = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_done)
        buttonAccept.doneLoadingAnimation(color, icon)
        Handler().postDelayed({ activity?.onBackPressed() }, 500)
    }

    override fun onError() {
        Toast.makeText(context, "Возникла неизвестная ошибка", Toast.LENGTH_LONG).show()
        val color = ContextCompat.getColor(ctx, R.color.error)
        val icon = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_error)
        buttonAccept.doneLoadingAnimation(color, icon)
        Handler().postDelayed({ buttonAccept.revertAnimation() }, 1000)
    }

    private fun getTextWatcher(onChanged: (s: CharSequence) -> Unit) =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { onChanged.invoke(s) }
            override fun afterTextChanged(s: Editable) {}
        }

    companion object {
        @JvmStatic
        fun newInstance(firstName: String?, lastName: String?, avatarPath: String?, headerPath: String?) =
                EditProfileFragment().apply { arguments = Bundle().apply {
                putString(BUNDLE_FIRST_NAME, firstName)
                putString(BUNDLE_LAST_NAME, lastName)
                putString(BUNDLE_AVATAR_PATH, avatarPath)
                putString(BUNDLE_HEADER_PATH, headerPath) }
        }
    }
}