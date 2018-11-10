/*
 * This file is part of the LIRE project: http://lire-project.net
 * LIRE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * LIRE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LIRE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * We kindly ask you to refer the any or one of the following publications in
 * any publication mentioning or employing Lire:
 *
 * Lux Mathias, Savvas A. Chatzichristofis. Lire: Lucene Image Retrieval –
 * An Extensible Java CBIR Library. In proceedings of the 16th ACM International
 * Conference on Multimedia, pp. 1085-1088, Vancouver, Canada, 2008
 * URL: http://doi.acm.org/10.1145/1459359.1459577
 *
 * Lux Mathias. Content Based Image Retrieval with LIRE. In proceedings of the
 * 19th ACM International Conference on Multimedia, pp. 735-738, Scottsdale,
 * Arizona, USA, 2011
 * URL: http://dl.acm.org/citation.cfm?id=2072432
 *
 * Mathias Lux, Oge Marques. Visual Information Retrieval using Java and LIRE
 * Morgan & Claypool, 2013
 * URL: http://www.morganclaypool.com/doi/abs/10.2200/S00468ED1V01Y201301ICR025
 *
 * Copyright statement:
 * ====================
 * (c) 2002-2013 by Mathias Lux (mathias@juggle.at)
 *  http://www.semanticmetadata.net/lire, http://www.lire-project.net
 *
 * Updated: 21.04.13 08:13
 */

package net.semanticmetadata.lire.sampleapp;

import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


public class Indexer {
    private CEDD cedd;
    private FCTH fcth;
    //private AutoColorCorrelogram autoColorCorrelogram;

    public Indexer() {
        cedd = new CEDD();
        fcth = new FCTH();
        //autoColorCorrelogram = new AutoColorCorrelogram();
    }

    public void indexImageFromURL(URL url) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("Download error");
            e.printStackTrace();
        }
        System.out.println("image dowloaded");

        cedd.extract(image);
        fcth.extract(image);
        //autoColorCorrelogram.extract(image);

        System.out.println("Finished indexing.");
    }

    public int compaire(Indexer ind) {
        double d1 = cedd.getDistance(ind.cedd);
        double d2 = fcth.getDistance(ind.fcth);
        //double d3 = autoColorCorrelogram.getDistance(ind.autoColorCorrelogram);
        System.out.println("CEDD len:" + d1);
        System.out.println("FCTH len:" + d2);

        double sum = (d1 + d2) / 2;
        if (sum < 10) return 0;
        if (sum < 30) return 1;
        return 2;
    }
}
