package ipleiria.pdm.homecoffee.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Classe CircleSliderView, estende da View.
 *
 * Possui métodos para criar e exibir uma view de um controle deslizante circular.
 *
 * Possibilita ajustar cor, tamanho e outras características.
 *
 * Possibilita adicionar um listener para detectar mudanças no tempo.
 *
 * Utiliza diversas variáveis e objetos do tipo Paint para desenhar elementos gráficos,
 *
 * como círculo, linha, botão e números.
 *
 * Utiliza variáveis para armazenar informações como posição, tamanho, cor.
 *
 * Utiliza variáveis para armazenar informações sobre o estado atual, como ângulo atual.
 */
public class CircleSliderView extends View {
    private static final String TAG = "CircleSliderView";

    // Status
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_RADIAN = "status_radian";

    // Default dimension in dp/pt
    private static final float DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 30;
    private static final float DEFAULT_NUMBER_SIZE = 10;
    private static final float DEFAULT_LINE_WIDTH = 0.5f;
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 15;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 1;
    private static final float DEFAULT_TIMER_NUMBER_SIZE = 38;
    private static final float DEFAULT_TIMER_TEXT_SIZE = 18;

    // Default color
    private static final int DEFAULT_CIRCLE_COLOR = 0xFFE9E2D9;
    private static final int DEFAULT_CIRCLE_BUTTON_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_LINE_COLOR = 0xFFFECE02;
    private static final int DEFAULT_HIGHLIGHT_LINE_COLOR = 0xFF68C5D7;
    private static final int DEFAULT_NUMBER_COLOR = 0xFF181318;
    private static final int DEFAULT_TIMER_NUMBER_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_TIMER_COLON_COLOR = 0xFFFA7777;
    private static final int DEFAULT_TIMER_TEXT_COLOR = 0x99F0F9FF;

    // Paint
    private Paint mCirclePaint;
    private Paint mHighlightLinePaint;
    private Paint mLinePaint;
    private Paint mCircleButtonPaint;
    private Paint mNumberPaint;
    private Paint mTimerNumberPaint;
    private Paint mTimerTextPaint;
    private Paint mTimerColonPaint;

    // Dimension
    private float mGapBetweenCircleAndLine;
    private float mNumberSize;
    private float mLineWidth;
    private float mCircleButtonRadius;
    private float mCircleStrokeWidth;
    private float mTimerNumberSize;
    private float mTimerTextSize;

    // Color
    private int mCircleColor;
    private int mCircleButtonColor;
    private int mLineColor;
    private int mHighlightLineColor;
    private int mNumberColor;
    private int mTimerNumberColor;
    private int mTimerTextColor;

    // Parameters
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mCurrentRadian;
    private float mCurrentRadian1;
    private float mPreRadian;
    private boolean mInCircleButton;
    private boolean mInCircleButton1;
    private boolean ismInCircleButton;
    private double mCurrentTime; // seconds



    private OnTimeChangedListener mListener;

    /**
     * Construtor da classe CircleSliderView que inicializa o componente com o contexto, atributos e estilo passados como parâmetros.
     * @param context Contexto da aplicação
     * @param attrs Atributos para configuração da visualização
     * @param defStyleAttr Estilo padrão para a visualização
     */
    public CircleSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    /**
     * Construtor da classe CircleSliderView que inicializa o componente com o contexto e atributos passados como parâmetros.
     * @param context Contexto da aplicação
     * @param attrs Atributos para configuração da visualização
     */
    public CircleSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Construtor da classe CircleSliderView que inicializa o componente com o contexto passado como parâmetro.
     * @param context Contexto da aplicação
     */
    public CircleSliderView(Context context) {
        this(context, null);
    }

    /**
     * Método que inicializa as configurações padrões do componente ou lê os atributos especificados em XML.
     */
    private void initialize() {
        Log.d(TAG, "initialize");
        // Set default dimension or read xml attributes
        mGapBetweenCircleAndLine = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE,
                getContext().getResources().getDisplayMetrics());
        mNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_NUMBER_SIZE, getContext().getResources()
                .getDisplayMetrics());
        mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, getContext().getResources()
                .getDisplayMetrics());
        mCircleButtonRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BUTTON_RADIUS, getContext()
                .getResources().getDisplayMetrics());
        mCircleStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_STROKE_WIDTH, getContext()
                .getResources().getDisplayMetrics());
        mTimerNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_NUMBER_SIZE, getContext()
                .getResources().getDisplayMetrics());
        mTimerTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_TEXT_SIZE, getContext()
                .getResources().getDisplayMetrics());

        // Set default color or read xml attributes
        mCircleColor = DEFAULT_CIRCLE_COLOR;
        mCircleButtonColor = DEFAULT_CIRCLE_BUTTON_COLOR;
        mLineColor = DEFAULT_LINE_COLOR;
        mHighlightLineColor = DEFAULT_HIGHLIGHT_LINE_COLOR;
        mNumberColor = DEFAULT_NUMBER_COLOR;
        mTimerNumberColor = DEFAULT_TIMER_NUMBER_COLOR;
        mTimerTextColor = DEFAULT_TIMER_TEXT_COLOR;

        // Init all paints
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerColonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // CirclePaint
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);

        // CircleButtonPaint
        mCircleButtonPaint.setColor(mCircleButtonColor);
        mCircleButtonPaint.setAntiAlias(true);
        mCircleButtonPaint.setStyle(Paint.Style.FILL);

        // LinePaint
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mCircleButtonRadius * 2 + 8);
        mLinePaint.setStyle(Paint.Style.STROKE);

        // HighlightLinePaint
        mHighlightLinePaint.setColor(mHighlightLineColor);
        mHighlightLinePaint.setStrokeWidth(mLineWidth);

        // NumberPaint
        mNumberPaint.setColor(mNumberColor);
        mNumberPaint.setTextSize(mNumberSize);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);
        mNumberPaint.setStyle(Paint.Style.STROKE);
        mNumberPaint.setStrokeWidth(mCircleButtonRadius * 2 + 8);

        // TimerNumberPaint
        mTimerNumberPaint.setColor(mTimerNumberColor);
        mTimerNumberPaint.setTextSize(mTimerNumberSize);
        mTimerNumberPaint.setTextAlign(Paint.Align.CENTER);

        // TimerTextPaint
        mTimerTextPaint.setColor(mTimerTextColor);
        mTimerTextPaint.setTextSize(mTimerTextSize);
        mTimerTextPaint.setTextAlign(Paint.Align.CENTER);

        // TimerColonPaint
        mTimerColonPaint.setColor(DEFAULT_TIMER_COLON_COLOR);
        mTimerColonPaint.setTextAlign(Paint.Align.CENTER);
        mTimerColonPaint.setTextSize(mTimerNumberSize);

        // Solve the target version related to shadow
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null); // use this, when targetSdkVersion is greater than or equal to api 14
    }
    /**
     * Método responsável por desenhar os elementos gráficos na tela
     * @param canvas O canvas onde os elementos serão desenhados
     */
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();

        canvas.drawCircle(mCx, mCy, mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine, mNumberPaint);
        canvas.save();
        canvas.rotate(-90, mCx, mCy);
        RectF rect = new RectF(mCx - (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine
        ), mCy - (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine
        ), mCx + (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine
        ), mCy + (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine
        ));

        if (mCurrentRadian1 > mCurrentRadian) {
            canvas.drawArc(rect, (float) Math.toDegrees(mCurrentRadian1), (float) Math.toDegrees(2 * (float) Math.PI) - (float) Math.toDegrees(mCurrentRadian1) + (float) Math.toDegrees(mCurrentRadian), false, mLinePaint);
        } else {
            canvas.drawArc(rect, (float) Math.toDegrees(mCurrentRadian1), (float) Math.toDegrees(mCurrentRadian) - (float) Math.toDegrees(mCurrentRadian1), false, mLinePaint);
        }
        canvas.restore();
        canvas.save();

        canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
        canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine
                , 0.01f, mLinePaint);
        canvas.restore();
        // TimerNumber
        canvas.save();
        canvas.rotate((float) Math.toDegrees(mCurrentRadian1), mCx, mCy);
        canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, 0.01f, mLinePaint);
        canvas.restore();
        // TimerNumber
        canvas.save();


        if (ismInCircleButton) {
            canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine
                    , mCircleButtonRadius, mCircleButtonPaint);
            canvas.restore();
            // TimerNumber
            canvas.save();
            canvas.rotate((float) Math.toDegrees(mCurrentRadian1), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mTimerColonPaint);
            canvas.restore();
            // TimerNumber
            canvas.save();
        } else {
            canvas.rotate((float) Math.toDegrees(mCurrentRadian1), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mTimerColonPaint);
            canvas.restore();
            // TimerNumber
            canvas.save();
            canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mCircleButtonPaint);
            canvas.restore();
            // TimerNumber
            canvas.save();
        }
        double i = mCurrentTime / 150;
        String oi = (i < 10 ? "0" + i : i) + ":" + ((mCurrentTime - 150 * i) * 10 / 25 < 10 ? "0" + ((mCurrentTime - 150 * i) * 10 / 25) : ((mCurrentTime - 150 * i) * 10 / 25));
//        canvas.drawText((i < 10 ? "0" + i : i) + " " + ((mCurrentTime - 150 * i) * 10 / 25 < 10 ?
//                "0" + ((mCurrentTime - 150 * i) * 10 / 25) : ((mCurrentTime - 150 * i) * 10 / 25)), mCx, mCy + getFontHeight(mTimerNumberPaint) / 2, mTimerNumberPaint);
        //canvas.drawText(setText(mCurrentTime), mCx, mCy + getFontHeight(mTimerNumberPaint) / 2, mTimerColonPaint);

        if (null != mListener) {
            if (ismInCircleButton) {
                mListener.start((i < 10 ? "0" + i : i) + ":" + ((mCurrentTime - 150 * i) * 10 / 25 < 10 ? "0" + ((mCurrentTime - 150 * i) * 10 / 25) : ((mCurrentTime - 150 * i) * 10 / 25)));
            } else {
                mListener.end((i < 10 ? "0" + i : i) + ":" + ((mCurrentTime - 150 * i) * 10 / 25 < 10 ? "0" + ((mCurrentTime - 150 * i) * 10 / 25) : ((mCurrentTime - 150 * i) * 10 / 25)));
            }
            canvas.drawText(mListener.setText(mCurrentTime), mCx, mCy + getFontHeight(mTimerNumberPaint) / 2, mTimerColonPaint);
        }

        canvas.restore();
        // Timer Text
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }

    /**
     * Este método é usado para calcular a altura da fonte. Ele é usado para ajustar a posição da numeração no círculo.
     * @param paint objeto Paint com as configurações da fonte usada para desenhar a numeração
     * @return altura da fonte
     */
    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    /**
     * Este método é chamado quando o usuário interage com o componente através de toques na tela.
     * Ele é usado para detectar se o usuário está tocando na área do botão de ajuste de tempo, e para calcular o ângulo de ajuste de tempo baseado na posição do toque.
     * @param event objeto MotionEvent com detalhes sobre o evento de toque
     * @return true para indicar que o evento foi tratado
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // If the point in the circle button
                if (mInCircleButton(event.getX(), event.getY()) && isEnabled()) {
                    mInCircleButton = true;
                    ismInCircleButton = false;
                    mPreRadian = getRadian(event.getX(), event.getY());
                    Log.d(TAG, "In circle button");
                } else if (mInCircleButton1(event.getX(), event.getY()) && isEnabled()) {
                    mInCircleButton1 = true;
                    ismInCircleButton = true;
                    mPreRadian = getRadian(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInCircleButton && isEnabled()) {
                    float temp = getRadian(event.getX(), event.getY());
                    if (mPreRadian > Math.toRadians(270) && temp < Math.toRadians(90)) {
                        mPreRadian -= 2 * Math.PI;
                    } else if (mPreRadian < Math.toRadians(90) && temp > Math.toRadians(270)) {
                        mPreRadian = (float) (temp + (temp - 2 * Math.PI) - mPreRadian);
                    }
                    mCurrentRadian += (temp - mPreRadian);
                    mPreRadian = temp;
                    if (mCurrentRadian > 2 * Math.PI) {
                        mCurrentRadian -= (float) (2 * Math.PI);
                    }
                    if (mCurrentRadian < 0) {
                        mCurrentRadian += (float) (2 * Math.PI);
                    }
                    mCurrentTime = (double) (60 / (2 * Math.PI) * mCurrentRadian * 60);
                    invalidate();
                } else if (mInCircleButton1 && isEnabled()) {
//                    float temp = getRadian(event.getX(), event.getY());
//                    if (mPreRadian > Math.toRadians(270) && temp < Math.toRadians(90)) {
//                        mPreRadian -= 2 * Math.PI;
//                    } else if (mPreRadian < Math.toRadians(90) && temp > Math.toRadians(270)) {
//                        mPreRadian = (float) (temp + (temp - 2 * Math.PI) - mPreRadian);
//                    }
//                    mCurrentRadian1 += (temp - mPreRadian);
//                    mPreRadian = temp;
//                    if (mCurrentRadian1 > 2 * Math.PI) {
//                        mCurrentRadian1 -= (float) (2 * Math.PI);
//                    }
//                    if (mCurrentRadian1 < 0) {
//                        mCurrentRadian1 += (float) (2 * Math.PI);
//                    }
//                    mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian1 * 60);
//                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mInCircleButton && isEnabled()) {
                    mInCircleButton = false;
                } else if (mInCircleButton1 && isEnabled()) {
                    mInCircleButton1 = false;
                }
                break;
        }
        return true;


    }

    /**
     * Verifica se o evento de clique está dentro do botão circular.
     * @param x - coordenada x do evento de clique
     * @param y - coordenada y do evento de clique
     * @return true se o evento de clique estiver dentro do botão circular, false caso contrário
     */
    // Whether the down event inside circle button
    private boolean mInCircleButton1(float x, float y) {
        float r = mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine;
        float x2 = (float) (mCx + r * Math.sin(mCurrentRadian1));
        float y2 = (float) (mCy - r * Math.cos(mCurrentRadian1));
        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) < mCircleButtonRadius) {
            return true;
        }
        return false;
    }

    /**
     *
     * Verifica se o evento de toque ocorreu dentro do botão de circulo.
     * @param x Coordenada x do evento de toque.
     * @param y Coordenada y do evento de toque.
     * @return true se o evento ocorreu dentro do botão de circulo, caso contrário false.
     */
    // Whether the down event inside circle button
    private boolean mInCircleButton(float x, float y) {
        float r = mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine;
        float x2 = (float) (mCx + r * Math.sin(mCurrentRadian));
        float y2 = (float) (mCy - r * Math.cos(mCurrentRadian));
        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) < mCircleButtonRadius) {
            return true;
        }
        return false;
    }


    /**
     * Método que utiliza trigonometria para calcular o radiano a partir de coordenadas x e y.
     * @param x Coordenada x
     * @param y Coordenada y
     * @return Radiano calculado.
     */
    // Use tri to cal radian
    private float getRadian(float x, float y) {
        float alpha = (float) Math.atan((x - mCx) / (mCy - y));
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI;
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI;
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (float) (2 * Math.PI + alpha);
        }
        return alpha;
    }

    /**
     * Sobrescrita do método onMeasure para garantir que a largura seja igual à altura e calcular
     * as posições e tamanhos necessários.
     * @param widthMeasureSpec Espaço disponível para largura
     * @param heightMeasureSpec Espaço disponível para altura
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Ensure width = height
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.mCx = width / 2;
        this.mCy = height / 2;
        // Radius
        if (mGapBetweenCircleAndLine + mCircleStrokeWidth >= mCircleButtonRadius) {
            this.mRadius = width / 2 - mCircleStrokeWidth / 2;
        } else {
            this.mRadius = width / 2 - (mCircleButtonRadius - mGapBetweenCircleAndLine -
                    mCircleStrokeWidth / 2);
        }
        setMeasuredDimension(width, height);
    }

    /**
     * Método chamado quando é necessário salvar o estado atual da View. Ele cria um Bundle, adiciona o estado parcelável da super classe e também adiciona o valor atual de mCurrentRadian.
     * @return Um objeto do tipo {@link Parcelable} contendo o estado atual da View.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian);
        return bundle;
    }

    /**
     * Método chamado quando é necessário restaurar o estado anterior da View.
     * Ele verifica se o estado é uma instância de Bundle, se sim, ele recupera o estado parcelável da super classe e o valor de mCurrentRadian.
     * Caso contrário, ele passa o estado para a super classe.
     * @param state O estado anterior da View que deve ser restaurado.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            mCurrentRadian = bundle.getFloat(STATUS_RADIAN);
            mCurrentTime = (double) (60 / (2 * Math.PI) * mCurrentRadian * 60);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * Método chamado quando o tamanho da View é alterado. Ele chama o método da super classe.
     * @param w Nova largura da View.
     * @param h Nova altura da View.
     * @param oldw Largura anterior da View.
     * @param oldh Altura anterior da View.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Método para definir o listener de mudança de tempo.
     * @param listener - Ouvinte de mudança de tempo
     */
    public void setOnTimeChangedListener(OnTimeChangedListener listener) {
        if (null != listener) {
            this.mListener = listener;
        }
    }

    /**
     * Interface que contém métodos chamados quando a hora é modificada.
     */
    public interface OnTimeChangedListener {
        double MAX_VALUE = 3600.0;

        /**
         * Chamado quando o tempo começa a ser modificado
         * @param starting - O tempo inicial
         */
        void start(String starting);

        /**
         * Chamado quando o tempo acaba de ser modificado
         * @param ending - O tempo final
         */
        void end(String ending);

        /**
         * Define o texto com o valor do tempo
         * @param value - o valor do tempo
         * @return String - o texto formatado
         */
        String setText(double value);
    }

    /**
     * @return Retorna o tempo atual
     */
    public double getmCurrentTime() {
        return mCurrentTime;
    }

    /**
     * Atualiza o tempo atual
     * @param mCurrentTime - novo tempo
     */
    public void setmCurrentTime(double mCurrentTime) {
        this.mCurrentTime = mCurrentTime;
    }

    /**
     * @return Retorna o radiano atual
     */
    public float getmCurrentRadian() {
        return mCurrentRadian;
    }

    /**
     * Atualiza o radiano atual
     * @param mCurrentRadian - novo radiano
     */
    public void setmCurrentRadian(float mCurrentRadian) {
        this.mCurrentRadian = mCurrentRadian;
    }
}

