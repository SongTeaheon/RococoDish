package com.example.front_ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewParent;

import org.jetbrains.annotations.NotNull;

import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

public final class Carousel implements ViewPager.PageTransformer {
    private int maxTranslateOffsetX;
    private ViewPager viewPager;

    public void transformPage(@NotNull View view, float position) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        ViewParent var10001;
        if (this.viewPager == null) {
            var10001 = view.getParent();
            if (var10001 == null) {
                throw new TypeCastException("null cannot be cast to non-null type androidx.viewpager.widget.ViewPager");
            }

            this.viewPager = (ViewPager)var10001;
        }

        if (this.viewPager == null) {
            var10001 = view.getParent();
            if (var10001 == null) {
                throw new TypeCastException("null cannot be cast to non-null type androidx.viewpager.widget.ViewPager");
            }

            this.viewPager = (ViewPager)var10001;
        }

        int var10000 = view.getLeft();
        ViewPager var9 = this.viewPager;
        if (var9 == null) {
            Intrinsics.throwNpe();
        }

        int leftInScreen = var10000 - var9.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        var9 = this.viewPager;
        if (var9 == null) {
            Intrinsics.throwNpe();
        }

        int offsetX = centerXInViewPager - var9.getMeasuredWidth() / 2;
        float var8 = (float)offsetX * 0.3F;
        var9 = this.viewPager;
        if (var9 == null) {
            Intrinsics.throwNpe();
        }

        float offsetRate = var8 / (float)var9.getMeasuredWidth();
        float scaleFactor = (float)1 - Math.abs(offsetRate);
        if (scaleFactor > (float)0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX((float)(-this.maxTranslateOffsetX) * offsetRate);
        }

        ViewCompat.setElevation(view, scaleFactor);
    }

    private final int dp2px(Context context, float dipValue) {
        Resources var10000 = context.getResources();
        Intrinsics.checkExpressionValueIsNotNull(var10000, "context.resources");
        float m = var10000.getDisplayMetrics().density;
        return (int)(dipValue * m + 0.5F);
    }

    public Carousel(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.maxTranslateOffsetX = this.dp2px(context, 180.0F);
    }
}
