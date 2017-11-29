package com.routee.qianbaotest.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.routee.qianbaotest.R;

public class PdfViewActivity extends AppCompatActivity {

    private PDFView mPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        mPdf = (PDFView) findViewById(R.id.pdf);
        mPdf.fromAsset("market.pdf")
                //                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
                //                .onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                //                .onDrawAll(onDrawListener)
                //                .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
                //                .onPageChange(onPageChangeListener)
                //                .onPageScroll(onPageScrollListener)
                //                .onError(onErrorListener)
                //                .onRender(onRenderListener) // called after document is rendered for the first time
                //                 called on single tap, return true if handled, false to toggle scroll handle visibility
                //                .onTap(onTapListener)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .spacing(100)
                .load();
    }
}
