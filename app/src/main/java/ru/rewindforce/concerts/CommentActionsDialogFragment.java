package ru.rewindforce.concerts;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.DialogFragment;
import ru.rewindforce.concerts.adapters.CommentsAdapter;

public class CommentActionsDialogFragment extends BottomSheetDialogFragment {

    private final static String BUNDLE_MODE = "bundle_mode",
                                BUNDLE_LIKE = "bundle_like",
                                BUNDLE_LOGIN = "bundle_login",
                                BUNDLE_UID = "bundle_comment_uid",
                                BUNDLE_POS = "bundle_position";
    private final static int MODE_OWNER = 0,
                             MODE_USER = 1,
                             MODE_ADMIN = 2;

    private String ownerLogin, commentUID;
    private int currentMode = 1, position;
    private boolean haveUserLikedComment = false;
    private LinearLayout actionLike, actionEdit, actionDelete, actionDismiss;
    private TextView actionLikeText, headerText;
    private ImageView actionLikeIcon;
    private CommentsAdapter.CommentActionsInterface onActionsListener;

    public static CommentActionsDialogFragment newInstance(String commentUID,
                                                           String commentOwnerUID, String commentOwnerLogin,
                                                           String userUID, String userLogin, String userRole,
                                                           boolean haveUserLikedComment, int position){
        CommentActionsDialogFragment fragment = new CommentActionsDialogFragment();
        Bundle bundle = new Bundle();
        if (commentOwnerUID.equals(userUID) && commentOwnerLogin.equals(userLogin)) {
            bundle.putInt(BUNDLE_MODE, MODE_OWNER);
        } else {
            if (userRole.equals("moderator") || userRole.equals("admin") || userRole.equals("god")) bundle.putInt(BUNDLE_MODE, MODE_ADMIN);
            else bundle.putInt(BUNDLE_MODE, MODE_USER);
        }
        bundle.putBoolean(BUNDLE_LIKE, haveUserLikedComment);
        bundle.putString(BUNDLE_LOGIN, commentOwnerLogin);
        bundle.putString(BUNDLE_UID, commentUID);
        bundle.putInt(BUNDLE_POS, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnActionsListener(CommentsAdapter.CommentActionsInterface listener) {
        onActionsListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            currentMode = getArguments().getInt(BUNDLE_MODE);
            haveUserLikedComment = getArguments().getBoolean(BUNDLE_LIKE);
            ownerLogin = getArguments().getString(BUNDLE_LOGIN);
            commentUID = getArguments().getString(BUNDLE_UID);
            position = getArguments().getInt(BUNDLE_POS);
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_comment_interaction, container, false);

        actionLike = view.findViewById(R.id.action_like);
        actionEdit = view.findViewById(R.id.action_edit);
        actionDelete = view.findViewById(R.id.action_delete);
        actionDismiss = view.findViewById(R.id.action_dismiss);
        actionLikeText = view.findViewById(R.id.action_like_text);
        actionLikeIcon = view.findViewById(R.id.action_like_icon);
        headerText = view.findViewById(R.id.text_header);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        headerText.setText(Html.fromHtml("Сообщение <b>@"+ownerLogin+"</b>"));

        View.OnClickListener onDeleteListener = (View v) -> {
            if (onActionsListener != null) onActionsListener.onDeleteComment(commentUID, position);
            dismiss();
        };

        View.OnClickListener onEditListener = (View v) -> {
            if (onActionsListener != null) onActionsListener.onEditComment(commentUID, position);
            dismiss();
        };

        if (currentMode == MODE_OWNER) {
            actionEdit.setVisibility(View.VISIBLE);
            actionDelete.setVisibility(View.VISIBLE);
            actionDelete.setOnClickListener(onDeleteListener);
            actionEdit.setOnClickListener(onEditListener);
        } else if (currentMode == MODE_ADMIN) {
            actionEdit.setVisibility(View.GONE);
            actionDelete.setVisibility(View.VISIBLE);
            actionDelete.setOnClickListener(onDeleteListener);
        } else {
            actionEdit.setVisibility(View.GONE);
            actionDelete.setVisibility(View.GONE);
        }

        if (haveUserLikedComment) {
            actionLike.setOnClickListener((View v) -> {
                if (onActionsListener != null) onActionsListener.onDislikeComment(commentUID, position);
                dismiss();
            });
            actionLikeText.setText("Дизлайкнуть");
            actionLikeIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_dislike));
        } else {
            actionLike.setOnClickListener((View v) -> {
                if (onActionsListener != null) onActionsListener.onLikeComment(commentUID, position);
                dismiss();
            });
            actionLikeText.setText("Лайкнуть");
            actionLikeIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_like));
        }

        actionDismiss.setOnClickListener((View v) -> dismiss());
    }
}
