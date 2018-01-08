package fyi.jackson.drew.roadquality.animation;


import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public abstract class MorphingFab {

    private final Context context;

    private View viewAfter; // thing fab morphs into
    private View viewBefore; // fab

    private AnimatedVectorDrawable avdIn, avdOut;

    private ImageView sharedElement;

    private boolean state = true; // before (false = after)

    public MorphingFab(Context context) {
        this.context = context;
    }

    public MorphingFab(Context context,
                       FloatingActionButton fab,
                       View viewAfter,
                       int sharedElementId,
                       int avdInId,
                       int avdOutId) {
        this.context = context;
        this.setFab(fab);
        this.setViewAfter(viewAfter);
        this.setSharedElement(sharedElementId);
        this.setAvd(avdInId, avdOutId);
    }

    public abstract boolean onFabClick(); // returning true morphs FAB, false does not

    public void setFab(FloatingActionButton fab) {
        this.viewBefore = fab;
        this.viewBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MorphingFab.this.open();
                MorphingFab.this.onFabClick();
            }
        });
    }

    public void setViewAfter(View view) {
        this.viewAfter = view;
        this.viewAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MorphingFab.this.close();
                MorphingFab.this.onFabClick();
            }
        });
    }

    public void setSharedElement(int id) {
        if (this.viewAfter == null) {
            throw new Error("viewAfter not yet defined");
        }
        this.sharedElement = this.viewAfter.findViewById(id);
    }

    public void setAvd(int inId, int outId) {
        AnimatedVectorDrawable in = (AnimatedVectorDrawable)
                context.getResources().getDrawable(inId);
        AnimatedVectorDrawable out = (AnimatedVectorDrawable)
                context.getResources().getDrawable(outId);
        this.avdIn = in;
        this.avdOut = out;
    }

    public void setAvd(int avdId) {
        this.setAvd(avdId, avdId);
    }


    private void setImageInitialPosition() {
        sharedElement.setX(
                viewBefore.getX() - ((sharedElement.getWidth() - viewBefore.getWidth()) / 2) - viewAfter.getX()
        );
        sharedElement.setY(
                viewBefore.getY() - ((sharedElement.getHeight() - viewBefore.getHeight()) / 2) - viewAfter.getY()
        );
    }

    public void open() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setImageInitialPosition();
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    viewAfter,
                    (int) (viewBefore.getX() + (viewBefore.getWidth() / 2) - viewAfter.getX()),
                    (int) (viewBefore.getY() + (viewBefore.getHeight() / 2) - viewAfter.getY()),
                    viewBefore.getWidth() / 2,
                    (float) Math.hypot(viewAfter.getWidth(), viewAfter.getHeight())
            );
            viewAfter.setVisibility(View.VISIBLE);
            viewBefore.setVisibility(View.INVISIBLE);
            sharedElement.setImageDrawable(avdIn);
            avdIn.start();
            sharedElement.animate()
                    .x(((viewAfter.getWidth() - sharedElement.getWidth()) / 2))
                    .y(((viewAfter.getHeight() - sharedElement.getHeight()) / 2))
                    .start();

            circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
            circularReveal.start();
            state = false;
        }
    }

    public void close() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    viewAfter,
                    (int) (viewBefore.getX() + (viewBefore.getWidth() / 2) - viewAfter.getX()),
                    (int) (viewBefore.getY() + (viewBefore.getHeight() / 2) - viewAfter.getY()),
                    (float) Math.hypot(viewAfter.getWidth(), viewAfter.getHeight()),
                    viewBefore.getWidth() / 2
            );
            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    viewAfter.setVisibility(View.GONE);
                    viewBefore.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            sharedElement.setImageDrawable(avdOut);
            avdOut.start();
            sharedElement.animate()
                    .x((viewBefore.getX()) - ((sharedElement.getWidth() - viewBefore.getWidth()) / 2) - viewAfter.getX())
                    .y((viewBefore.getY()) - ((sharedElement.getHeight() - viewBefore.getHeight()) / 2) - viewAfter.getY())
                    .start();

            circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
            circularReveal.start();
            state = true;
        }
    }

    public boolean isOpen() {
        return !state;
    }

    public boolean isClosed() {
        return state;
    }

}
