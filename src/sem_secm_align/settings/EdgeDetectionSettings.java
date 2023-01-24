/*
 * Created: 2023-01-11
 * Updated: 2023-01-13
 * Nathaniel Leslie
 */
package sem_secm_align.settings;

import java.awt.Color;
import java.awt.Font;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.Gauss3;
import sem_secm_align.utility.filters.Gauss5;
import sem_secm_align.utility.filters.Gauss7;
import sem_secm_align.utility.filters.Identity;
import sem_secm_align.utility.filters.Median3;

/**
 * Contains settings that are common between the
 * {@link sem_secm_align.edge_detection.EdgeDetectionWindow EdgeDetectionWindow}, {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram}
 * and {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer}
 * classes.
 *
 * @author Nathaniel
 */
public class EdgeDetectionSettings {

    /**
     * Initializes the settings.
     */
    public EdgeDetectionSettings() {
        DEFAULT_DISPLAY_MODE = 0;
        DEFAULT_FILTER = 0;
        DEFAULT_IMAGE_SOURCE = 0;
        DOMAIN_SELECTION_COLOR = Color.LIGHT_GRAY;
        BACKGROUND_COLOR = Color.WHITE;
        BAR_FILL_COLOR = Color.YELLOW;
        BAR_OUTLINE_COLOR = Color.BLACK;
        TEXT_COLOR = Color.BLACK;
        AXES_COLOR = Color.BLACK;
        LABEL_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        DISPLAY_OPTIONS = initDisplayList();
        IMAGE_SOURCE_OPTIONS = initImageSourceList();
        FILTER_OPTIONS = new Filter[]{new Identity(), new Gauss3(), new Gauss5(), new Gauss7(), new Median3()};
        COLOR_MODE = ColourSettings.CSCALE_GREY_MAGENTAZERO;
    }

    /**
     * Creates an array of Strings describing the different display modes. This
     * will be used for
     * {@link sem_secm_align.edge_detection.EdgeDetectionWindow#display_options EdgeDetectionWindow.display_options}.
     *
     * @return Descriptive names of the available display modes as an array of
     * Strings.
     * @see #DISPLAY_MODE_PRE_FILTER
     * @see #DISPLAY_MODE_POST_FILTER
     * @see #DISPLAY_MODE_EDGE_MAGNITUDES
     * @see #DISPLAY_MODE_THRESHOLDED_EDGES
     */
    private String[] initDisplayList() {
        String[] display_options = new String[4];
        display_options[DISPLAY_MODE_PRE_FILTER] = "Signals (pre-filtering)";
        display_options[DISPLAY_MODE_POST_FILTER] = "Signals (post-filtering)";
        display_options[DISPLAY_MODE_EDGE_MAGNITUDES] = "Edges (pre-thresholding)";
        display_options[DISPLAY_MODE_THRESHOLDED_EDGES] = "Edges (post-thresholding)";
        return display_options;
    }

    /**
     * Creates an array of Strings describing the sources of image for edge
     * detection. This will be used for
     * {@link sem_secm_align.edge_detection.EdgeDetectionWindow#image_sources EdgeDetectionWindow.image_sources}.
     * @return Descriptive names of the available image sources as an array of
     * Strings.
     * @see #IMAGE_SOURCE_SECM
     * @see #IMAGE_SOURCE_SEM
     */
    private String[] initImageSourceList() {
        String[] imagesource_options = new String[2];
        imagesource_options[IMAGE_SOURCE_SECM] = "SECM";
        imagesource_options[IMAGE_SOURCE_SEM] = "SEM";
        return imagesource_options;
    }

    /**
     * The initially selected display mode.
     * @see #DISPLAY_MODE_PRE_FILTER
     * @see #DISPLAY_MODE_POST_FILTER
     * @see #DISPLAY_MODE_EDGE_MAGNITUDES
     * @see #DISPLAY_MODE_THRESHOLDED_EDGES
     */
    public final int DEFAULT_DISPLAY_MODE;
    /**
     * The initially selected {@link #FILTER_OPTIONS noise filter}
     */
    public final int DEFAULT_FILTER;
    /**
     * The initially selected image source.
     * @see #IMAGE_SOURCE_SECM
     * @see #IMAGE_SOURCE_SEM
     */
    public final int DEFAULT_IMAGE_SOURCE;
    /**
     * The background colour of the {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram's} histogram within the thresholded range.
     */
    public final Color DOMAIN_SELECTION_COLOR;
    /**
     * The background colour of the plots of the {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram} and {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer}
     */
    public final Color BACKGROUND_COLOR;
    /**
     * The fill colour of the bars shown by the associated {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram}.
     */
    public final Color BAR_FILL_COLOR;
    /**
     * The outline colour of the bars shown by the associated {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram}.
     */
    public final Color BAR_OUTLINE_COLOR;
    /**
     * The colour of the text of the {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram} and {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer}
     */
    public final Color TEXT_COLOR;
    /**
     * The colour of the axes of the {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram} and {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer}
     */
    public final Color AXES_COLOR;
    /**
     * The font used by the {@link sem_secm_align.edge_detection.EdgeHistogram EdgeHistogram} and {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer}
     */
    public final Font LABEL_FONT;
    /**
     * The items for {@link sem_secm_align.edge_detection.EdgeDetectionWindow#display_options EdgeDetectionWindow.display_options}.
     * @see #initDisplayList() 
     */
    public final String[] DISPLAY_OPTIONS;
    /**
     * The items for {@link sem_secm_align.edge_detection.EdgeDetectionWindow#image_sources EdgeDetectionWindow.image_sources}.
     * @see #initImageSourceList() 
     */
    public final String[] IMAGE_SOURCE_OPTIONS;
    /**
     * The items for {@link sem_secm_align.edge_detection.EdgeDetectionWindow#filter_options EdgeDetectionWindow.filter_options}.
     */
    public final Filter[] FILTER_OPTIONS;
    /**
     * The colour mode for the {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer's} image plots.
     * @see sem_secm_align.settings.ColourSettings#colourScale(double, int) 
     * @see sem_secm_align.settings.ColourSettings#CSCALE_GREY
     * @see sem_secm_align.settings.ColourSettings#CSCALE_GREY_MAGENTAZERO
     * @see sem_secm_align.settings.ColourSettings#CSCALE_RED_BLACKZERO
     */
    public final int COLOR_MODE;

    /**
     * Indicates that the associated
     * {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer} is to
     * display the image with sampling according to the specified
     * {@link sem_secm_align.Visualizer#reac_xresolution x-} and
     * {@link sem_secm_align.Visualizer#reac_yresolution y-resolutions}.
     */
    public static final int DISPLAY_MODE_PRE_FILTER = 0;
    /**
     * Indicates that the associated
     * {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer} is to
     * display the image after it has been filtered, but before edge detection.
     */
    public static final int DISPLAY_MODE_POST_FILTER = 1;
    /**
     * Indicates that the associated
     * {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer} is to
     * display the image after edge detection, but before thresholding.
     */
    public static final int DISPLAY_MODE_EDGE_MAGNITUDES = 2;
    /**
     * Indicates that the associated
     * {@link sem_secm_align.edge_detection.EdgeVisualizer EdgeVisualizer} is to
     * display the image after applying the threshold to the detected edges.
     */
    public static final int DISPLAY_MODE_THRESHOLDED_EDGES = 3;

    /**
     * Indicates that edges are to be detected from the {@link sem_secm_align.Visualizer#secm_image SECM image}.
     */
    public static final int IMAGE_SOURCE_SECM = 0;
    /**
     * Indicates that edges are to be detected from the {@link sem_secm_align.Visualizer#sem_image SEM image}.
     */
    public static final int IMAGE_SOURCE_SEM = 1;
    /**
     * This is a reserved image source designation.
     * @see sem_secm_align.edge_detection.EdgeDetectionWindow#applyDetection() 
     */
    public static final int IMAGE_SOURCE_NONE = -1;
}
