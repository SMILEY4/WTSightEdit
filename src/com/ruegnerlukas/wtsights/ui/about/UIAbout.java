package com.ruegnerlukas.wtsights.ui.about;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.UICalibrationEditor;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;
import com.ruegnerlukas.wtutils.Config2;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UIAbout {

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	@FXML private TextArea textArea;
	
	
	public static void openNew(Stage stage) {
		
		Logger.get().info("Opening UIAbout");
		
		try {

			final Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(window);

			FXMLLoader loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_about.fxml"));
			Parent root = (Parent) loader.load();
			UIAbout controller = (UIAbout) loader.getController();
			
			Scene scene = new Scene(root, 600, 700, true, SceneAntialiasing.DISABLED);
			
			window.setTitle("WT Sight Editor");
			if(WTSights.DARK_MODE) {
				scene.getStylesheets().add("/ui/modena_dark.css");
			}
			window.setScene(scene);
			window.show();
			
			controller.create(window);

		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		
	}
	
	
	
	
	private void create(Stage stage) {
		
		textArea.setText(""+
				"Product Version\r\n" + 
				"    " + "WT Sight Editor v" + Config2.build_version + "\r\n" + 
				"\r\n" + 
				"Build Information\r\n" + 
				"    " + "Version "  + Config2.build_version + "\r\n" + 
				"    " + "Date: " + Config2.build_date + "\r\n" + 
				"\r\n" + 
				"Java version (Installed): " + "\r\n" + 
				"    " + SystemUtils.getJavaRuntimeName() + " "+ SystemUtils.getJavaRuntimeVersion() + "\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" +
				"\r\n" + 
				"\r\n" + 
				"THIRD-PARTY-LIBRARIES:" +
				"\r\n" + 
				"\r\n" + 
				"The following software may be included in this product: apache-commons-math.\r\n" + 
				"This software contains the following license and notice below:\r\n" + 
				"\r\n" + 
				"Apache Commons Math\r\n" + 
				"Copyright 2001-2016 The Apache Software Foundation\r\n" + 
				"\r\n" + 
				"This product includes software developed at\r\n" + 
				"The Apache Software Foundation (http://www.apache.org/).\r\n" + 
				"\r\n" + 
				"This product includes software developed for Orekit by\r\n" + 
				"CS Systèmes d'Information (http://www.c-s.fr/)\r\n" + 
				"Copyright 2010-2012 CS Systèmes d'Information\r\n" + 
				"\r\n" + 
				"                                 Apache License\r\n" + 
				"                           Version 2.0, January 2004\r\n" + 
				"                        http://www.apache.org/licenses/\r\n" + 
				"\r\n" + 
				"   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\r\n" + 
				"\r\n" + 
				"   1. Definitions.\r\n" + 
				"\r\n" + 
				"      \"License\" shall mean the terms and conditions for use, reproduction,\r\n" + 
				"      and distribution as defined by Sections 1 through 9 of this document.\r\n" + 
				"\r\n" + 
				"      \"Licensor\" shall mean the copyright owner or entity authorized by\r\n" + 
				"      the copyright owner that is granting the License.\r\n" + 
				"\r\n" + 
				"      \"Legal Entity\" shall mean the union of the acting entity and all\r\n" + 
				"      other entities that control, are controlled by, or are under common\r\n" + 
				"      control with that entity. For the purposes of this definition,\r\n" + 
				"      \"control\" means (i) the power, direct or indirect, to cause the\r\n" + 
				"      direction or management of such entity, whether by contract or\r\n" + 
				"      otherwise, or (ii) ownership of fifty percent (50%) or more of the\r\n" + 
				"      outstanding shares, or (iii) beneficial ownership of such entity.\r\n" + 
				"\r\n" + 
				"      \"You\" (or \"Your\") shall mean an individual or Legal Entity\r\n" + 
				"      exercising permissions granted by this License.\r\n" + 
				"\r\n" + 
				"      \"Source\" form shall mean the preferred form for making modifications,\r\n" + 
				"      including but not limited to software source code, documentation\r\n" + 
				"      source, and configuration files.\r\n" + 
				"\r\n" + 
				"      \"Object\" form shall mean any form resulting from mechanical\r\n" + 
				"      transformation or translation of a Source form, including but\r\n" + 
				"      not limited to compiled object code, generated documentation,\r\n" + 
				"      and conversions to other media types.\r\n" + 
				"\r\n" + 
				"      \"Work\" shall mean the work of authorship, whether in Source or\r\n" + 
				"      Object form, made available under the License, as indicated by a\r\n" + 
				"      copyright notice that is included in or attached to the work\r\n" + 
				"      (an example is provided in the Appendix below).\r\n" + 
				"\r\n" + 
				"      \"Derivative Works\" shall mean any work, whether in Source or Object\r\n" + 
				"      form, that is based on (or derived from) the Work and for which the\r\n" + 
				"      editorial revisions, annotations, elaborations, or other modifications\r\n" + 
				"      represent, as a whole, an original work of authorship. For the purposes\r\n" + 
				"      of this License, Derivative Works shall not include works that remain\r\n" + 
				"      separable from, or merely link (or bind by name) to the interfaces of,\r\n" + 
				"      the Work and Derivative Works thereof.\r\n" + 
				"\r\n" + 
				"      \"Contribution\" shall mean any work of authorship, including\r\n" + 
				"      the original version of the Work and any modifications or additions\r\n" + 
				"      to that Work or Derivative Works thereof, that is intentionally\r\n" + 
				"      submitted to Licensor for inclusion in the Work by the copyright owner\r\n" + 
				"      or by an individual or Legal Entity authorized to submit on behalf of\r\n" + 
				"      the copyright owner. For the purposes of this definition, \"submitted\"\r\n" + 
				"      means any form of electronic, verbal, or written communication sent\r\n" + 
				"      to the Licensor or its representatives, including but not limited to\r\n" + 
				"      communication on electronic mailing lists, source code control systems,\r\n" + 
				"      and issue tracking systems that are managed by, or on behalf of, the\r\n" + 
				"      Licensor for the purpose of discussing and improving the Work, but\r\n" + 
				"      excluding communication that is conspicuously marked or otherwise\r\n" + 
				"      designated in writing by the copyright owner as \"Not a Contribution.\"\r\n" + 
				"\r\n" + 
				"      \"Contributor\" shall mean Licensor and any individual or Legal Entity\r\n" + 
				"      on behalf of whom a Contribution has been received by Licensor and\r\n" + 
				"      subsequently incorporated within the Work.\r\n" + 
				"\r\n" + 
				"   2. Grant of Copyright License. Subject to the terms and conditions of\r\n" + 
				"      this License, each Contributor hereby grants to You a perpetual,\r\n" + 
				"      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\r\n" + 
				"      copyright license to reproduce, prepare Derivative Works of,\r\n" + 
				"      publicly display, publicly perform, sublicense, and distribute the\r\n" + 
				"      Work and such Derivative Works in Source or Object form.\r\n" + 
				"\r\n" + 
				"   3. Grant of Patent License. Subject to the terms and conditions of\r\n" + 
				"      this License, each Contributor hereby grants to You a perpetual,\r\n" + 
				"      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\r\n" + 
				"      (except as stated in this section) patent license to make, have made,\r\n" + 
				"      use, offer to sell, sell, import, and otherwise transfer the Work,\r\n" + 
				"      where such license applies only to those patent claims licensable\r\n" + 
				"      by such Contributor that are necessarily infringed by their\r\n" + 
				"      Contribution(s) alone or by combination of their Contribution(s)\r\n" + 
				"      with the Work to which such Contribution(s) was submitted. If You\r\n" + 
				"      institute patent litigation against any entity (including a\r\n" + 
				"      cross-claim or counterclaim in a lawsuit) alleging that the Work\r\n" + 
				"      or a Contribution incorporated within the Work constitutes direct\r\n" + 
				"      or contributory patent infringement, then any patent licenses\r\n" + 
				"      granted to You under this License for that Work shall terminate\r\n" + 
				"      as of the date such litigation is filed.\r\n" + 
				"\r\n" + 
				"   4. Redistribution. You may reproduce and distribute copies of the\r\n" + 
				"      Work or Derivative Works thereof in any medium, with or without\r\n" + 
				"      modifications, and in Source or Object form, provided that You\r\n" + 
				"      meet the following conditions:\r\n" + 
				"\r\n" + 
				"      (a) You must give any other recipients of the Work or\r\n" + 
				"          Derivative Works a copy of this License; and\r\n" + 
				"\r\n" + 
				"      (b) You must cause any modified files to carry prominent notices\r\n" + 
				"          stating that You changed the files; and\r\n" + 
				"\r\n" + 
				"      (c) You must retain, in the Source form of any Derivative Works\r\n" + 
				"          that You distribute, all copyright, patent, trademark, and\r\n" + 
				"          attribution notices from the Source form of the Work,\r\n" + 
				"          excluding those notices that do not pertain to any part of\r\n" + 
				"          the Derivative Works; and\r\n" + 
				"\r\n" + 
				"      (d) If the Work includes a \"NOTICE\" text file as part of its\r\n" + 
				"          distribution, then any Derivative Works that You distribute must\r\n" + 
				"          include a readable copy of the attribution notices contained\r\n" + 
				"          within such NOTICE file, excluding those notices that do not\r\n" + 
				"          pertain to any part of the Derivative Works, in at least one\r\n" + 
				"          of the following places: within a NOTICE text file distributed\r\n" + 
				"          as part of the Derivative Works; within the Source form or\r\n" + 
				"          documentation, if provided along with the Derivative Works; or,\r\n" + 
				"          within a display generated by the Derivative Works, if and\r\n" + 
				"          wherever such third-party notices normally appear. The contents\r\n" + 
				"          of the NOTICE file are for informational purposes only and\r\n" + 
				"          do not modify the License. You may add Your own attribution\r\n" + 
				"          notices within Derivative Works that You distribute, alongside\r\n" + 
				"          or as an addendum to the NOTICE text from the Work, provided\r\n" + 
				"          that such additional attribution notices cannot be construed\r\n" + 
				"          as modifying the License.\r\n" + 
				"\r\n" + 
				"      You may add Your own copyright statement to Your modifications and\r\n" + 
				"      may provide additional or different license terms and conditions\r\n" + 
				"      for use, reproduction, or distribution of Your modifications, or\r\n" + 
				"      for any such Derivative Works as a whole, provided Your use,\r\n" + 
				"      reproduction, and distribution of the Work otherwise complies with\r\n" + 
				"      the conditions stated in this License.\r\n" + 
				"\r\n" + 
				"   5. Submission of Contributions. Unless You explicitly state otherwise,\r\n" + 
				"      any Contribution intentionally submitted for inclusion in the Work\r\n" + 
				"      by You to the Licensor shall be under the terms and conditions of\r\n" + 
				"      this License, without any additional terms or conditions.\r\n" + 
				"      Notwithstanding the above, nothing herein shall supersede or modify\r\n" + 
				"      the terms of any separate license agreement you may have executed\r\n" + 
				"      with Licensor regarding such Contributions.\r\n" + 
				"\r\n" + 
				"   6. Trademarks. This License does not grant permission to use the trade\r\n" + 
				"      names, trademarks, service marks, or product names of the Licensor,\r\n" + 
				"      except as required for reasonable and customary use in describing the\r\n" + 
				"      origin of the Work and reproducing the content of the NOTICE file.\r\n" + 
				"\r\n" + 
				"   7. Disclaimer of Warranty. Unless required by applicable law or\r\n" + 
				"      agreed to in writing, Licensor provides the Work (and each\r\n" + 
				"      Contributor provides its Contributions) on an \"AS IS\" BASIS,\r\n" + 
				"      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\r\n" + 
				"      implied, including, without limitation, any warranties or conditions\r\n" + 
				"      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\r\n" + 
				"      PARTICULAR PURPOSE. You are solely responsible for determining the\r\n" + 
				"      appropriateness of using or redistributing the Work and assume any\r\n" + 
				"      risks associated with Your exercise of permissions under this License.\r\n" + 
				"\r\n" + 
				"   8. Limitation of Liability. In no event and under no legal theory,\r\n" + 
				"      whether in tort (including negligence), contract, or otherwise,\r\n" + 
				"      unless required by applicable law (such as deliberate and grossly\r\n" + 
				"      negligent acts) or agreed to in writing, shall any Contributor be\r\n" + 
				"      liable to You for damages, including any direct, indirect, special,\r\n" + 
				"      incidental, or consequential damages of any character arising as a\r\n" + 
				"      result of this License or out of the use or inability to use the\r\n" + 
				"      Work (including but not limited to damages for loss of goodwill,\r\n" + 
				"      work stoppage, computer failure or malfunction, or any and all\r\n" + 
				"      other commercial damages or losses), even if such Contributor\r\n" + 
				"      has been advised of the possibility of such damages.\r\n" + 
				"\r\n" + 
				"   9. Accepting Warranty or Additional Liability. While redistributing\r\n" + 
				"      the Work or Derivative Works thereof, You may choose to offer,\r\n" + 
				"      and charge a fee for, acceptance of support, warranty, indemnity,\r\n" + 
				"      or other liability obligations and/or rights consistent with this\r\n" + 
				"      License. However, in accepting such obligations, You may act only\r\n" + 
				"      on Your own behalf and on Your sole responsibility, not on behalf\r\n" + 
				"      of any other Contributor, and only if You agree to indemnify,\r\n" + 
				"      defend, and hold each Contributor harmless for any liability\r\n" + 
				"      incurred by, or claims asserted against, such Contributor by reason\r\n" + 
				"      of your accepting any such warranty or additional liability.\r\n" + 
				"\r\n" + 
				"   END OF TERMS AND CONDITIONS\r\n" + 
				"\r\n" + 
				"   APPENDIX: How to apply the Apache License to your work.\r\n" + 
				"\r\n" + 
				"      To apply the Apache License to your work, attach the following\r\n" + 
				"      boilerplate notice, with the fields enclosed by brackets \"[]\"\r\n" + 
				"      replaced with your own identifying information. (Don't include\r\n" + 
				"      the brackets!)  The text should be enclosed in the appropriate\r\n" + 
				"      comment syntax for the file format. We also recommend that a\r\n" + 
				"      file or class name and description of purpose be included on the\r\n" + 
				"      same \"printed page\" as the copyright notice for easier\r\n" + 
				"      identification within third-party archives.\r\n" + 
				"\r\n" + 
				"   Copyright [yyyy] [name of copyright owner]\r\n" + 
				"\r\n" + 
				"   Licensed under the Apache License, Version 2.0 (the \"License\");\r\n" + 
				"   you may not use this file except in compliance with the License.\r\n" + 
				"   You may obtain a copy of the License at\r\n" + 
				"\r\n" + 
				"       http://www.apache.org/licenses/LICENSE-2.0\r\n" + 
				"\r\n" + 
				"   Unless required by applicable law or agreed to in writing, software\r\n" + 
				"   distributed under the License is distributed on an \"AS IS\" BASIS,\r\n" + 
				"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\r\n" + 
				"   See the License for the specific language governing permissions and\r\n" + 
				"   limitations under the License.\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"Apache Commons Math includes the following code provided to the ASF under the\r\n" + 
				"Apache License 2.0:\r\n" + 
				"\r\n" + 
				" - The inverse error function implementation in the Erf class is based on CUDA\r\n" + 
				"   code developed by Mike Giles, Oxford-Man Institute of Quantitative Finance,\r\n" + 
				"   and published in GPU Computing Gems, volume 2, 2010 (grant received on\r\n" + 
				"   March 23th 2013)\r\n" + 
				" - The LinearConstraint, LinearObjectiveFunction, LinearOptimizer,\r\n" + 
				"   RelationShip, SimplexSolver and SimplexTableau classes in package\r\n" + 
				"   org.apache.commons.math3.optimization.linear include software developed by\r\n" + 
				"   Benjamin McCann (http://www.benmccann.com) and distributed with\r\n" + 
				"   the following copyright: Copyright 2009 Google Inc. (grant received on\r\n" + 
				"   March 16th 2009)\r\n" + 
				" - The class \"org.apache.commons.math3.exception.util.LocalizedFormatsTest\" which\r\n" + 
				"   is an adapted version of \"OrekitMessagesTest\" test class for the Orekit library\r\n" + 
				" - The \"org.apache.commons.math3.analysis.interpolation.HermiteInterpolator\"\r\n" + 
				"   has been imported from the Orekit space flight dynamics library.\r\n" + 
				"\r\n" + 
				"===============================================================================\r\n" + 
				" \r\n" + 
				"\r\n" + 
				"\r\n" + 
				"APACHE COMMONS MATH DERIVATIVE WORKS: \r\n" + 
				"\r\n" + 
				"The Apache commons-math library includes a number of subcomponents\r\n" + 
				"whose implementation is derived from original sources written\r\n" + 
				"in C or Fortran.  License terms of the original sources\r\n" + 
				"are reproduced below.\r\n" + 
				"\r\n" + 
				"===============================================================================\r\n" + 
				"For the lmder, lmpar and qrsolv Fortran routine from minpack and translated in\r\n" + 
				"the LevenbergMarquardtOptimizer class in package\r\n" + 
				"org.apache.commons.math3.optimization.general \r\n" + 
				"Original source copyright and license statement:\r\n" + 
				"\r\n" + 
				"Minpack Copyright Notice (1999) University of Chicago.  All rights reserved\r\n" + 
				"\r\n" + 
				"Redistribution and use in source and binary forms, with or\r\n" + 
				"without modification, are permitted provided that the\r\n" + 
				"following conditions are met:\r\n" + 
				"\r\n" + 
				"1. Redistributions of source code must retain the above\r\n" + 
				"copyright notice, this list of conditions and the following\r\n" + 
				"disclaimer.\r\n" + 
				"\r\n" + 
				"2. Redistributions in binary form must reproduce the above\r\n" + 
				"copyright notice, this list of conditions and the following\r\n" + 
				"disclaimer in the documentation and/or other materials\r\n" + 
				"provided with the distribution.\r\n" + 
				"\r\n" + 
				"3. The end-user documentation included with the\r\n" + 
				"redistribution, if any, must include the following\r\n" + 
				"acknowledgment:\r\n" + 
				"\r\n" + 
				"   \"This product includes software developed by the\r\n" + 
				"   University of Chicago, as Operator of Argonne National\r\n" + 
				"   Laboratory.\r\n" + 
				"\r\n" + 
				"Alternately, this acknowledgment may appear in the software\r\n" + 
				"itself, if and wherever such third-party acknowledgments\r\n" + 
				"normally appear.\r\n" + 
				"\r\n" + 
				"4. WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED \"AS IS\"\r\n" + 
				"WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE\r\n" + 
				"UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND\r\n" + 
				"THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR\r\n" + 
				"IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES\r\n" + 
				"OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE\r\n" + 
				"OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY\r\n" + 
				"OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR\r\n" + 
				"USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF\r\n" + 
				"THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)\r\n" + 
				"DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION\r\n" + 
				"UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL\r\n" + 
				"BE CORRECTED.\r\n" + 
				"\r\n" + 
				"5. LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT\r\n" + 
				"HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF\r\n" + 
				"ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,\r\n" + 
				"INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF\r\n" + 
				"ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF\r\n" + 
				"PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER\r\n" + 
				"SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT\r\n" + 
				"(INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,\r\n" + 
				"EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE\r\n" + 
				"POSSIBILITY OF SUCH LOSS OR DAMAGES.\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"Copyright and license statement for the odex Fortran routine developed by\r\n" + 
				"E. Hairer and G. Wanner and translated in GraggBulirschStoerIntegrator class\r\n" + 
				"in package org.apache.commons.math3.ode.nonstiff:\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"Copyright (c) 2004, Ernst Hairer\r\n" + 
				"\r\n" + 
				"Redistribution and use in source and binary forms, with or without \r\n" + 
				"modification, are permitted provided that the following conditions are \r\n" + 
				"met:\r\n" + 
				"\r\n" + 
				"- Redistributions of source code must retain the above copyright \r\n" + 
				"notice, this list of conditions and the following disclaimer.\r\n" + 
				"\r\n" + 
				"- Redistributions in binary form must reproduce the above copyright \r\n" + 
				"notice, this list of conditions and the following disclaimer in the \r\n" + 
				"documentation and/or other materials provided with the distribution.\r\n" + 
				"\r\n" + 
				"THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS \r\n" + 
				"IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED \r\n" + 
				"TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A \r\n" + 
				"PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR \r\n" + 
				"CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, \r\n" + 
				"EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, \r\n" + 
				"PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR \r\n" + 
				"PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF \r\n" + 
				"LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING \r\n" + 
				"NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS \r\n" + 
				"SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"Copyright and license statement for the original Mersenne twister C\r\n" + 
				"routines translated in MersenneTwister class in package \r\n" + 
				"org.apache.commons.math3.random:\r\n" + 
				"\r\n" + 
				"   Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura,\r\n" + 
				"   All rights reserved.                          \r\n" + 
				"\r\n" + 
				"   Redistribution and use in source and binary forms, with or without\r\n" + 
				"   modification, are permitted provided that the following conditions\r\n" + 
				"   are met:\r\n" + 
				"\r\n" + 
				"     1. Redistributions of source code must retain the above copyright\r\n" + 
				"        notice, this list of conditions and the following disclaimer.\r\n" + 
				"\r\n" + 
				"     2. Redistributions in binary form must reproduce the above copyright\r\n" + 
				"        notice, this list of conditions and the following disclaimer in the\r\n" + 
				"        documentation and/or other materials provided with the distribution.\r\n" + 
				"\r\n" + 
				"     3. The names of its contributors may not be used to endorse or promote \r\n" + 
				"        products derived from this software without specific prior written \r\n" + 
				"        permission.\r\n" + 
				"\r\n" + 
				"   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS\r\n" + 
				"   \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT\r\n" + 
				"   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR\r\n" + 
				"   A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR\r\n" + 
				"   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,\r\n" + 
				"   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,\r\n" + 
				"   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR\r\n" + 
				"   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF\r\n" + 
				"   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\r\n" + 
				"   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\r\n" + 
				"   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\r\n" + 
				"\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"The initial code for shuffling an array (originally in class\r\n" + 
				"\"org.apache.commons.math3.random.RandomDataGenerator\", now replaced by\r\n" + 
				"a method in class \"org.apache.commons.math3.util.MathArrays\") was\r\n" + 
				"inspired from the algorithm description provided in\r\n" + 
				"\"Algorithms\", by Ian Craw and John Pulham (University of Aberdeen 1999).\r\n" + 
				"The textbook (containing a proof that the shuffle is uniformly random) is\r\n" + 
				"available here:\r\n" + 
				"  http://citeseerx.ist.psu.edu/viewdoc/download;?doi=10.1.1.173.1898&rep=rep1&type=pdf\r\n" + 
				"\r\n" + 
				"===============================================================================\r\n" + 
				"License statement for the direction numbers in the resource files for Sobol sequences.\r\n" + 
				"\r\n" + 
				"-----------------------------------------------------------------------------\r\n" + 
				"Licence pertaining to sobol.cc and the accompanying sets of direction numbers\r\n" + 
				"\r\n" + 
				"-----------------------------------------------------------------------------\r\n" + 
				"Copyright (c) 2008, Frances Y. Kuo and Stephen Joe\r\n" + 
				"All rights reserved.\r\n" + 
				"\r\n" + 
				"Redistribution and use in source and binary forms, with or without\r\n" + 
				"modification, are permitted provided that the following conditions are met:\r\n" + 
				"\r\n" + 
				"    * Redistributions of source code must retain the above copyright\r\n" + 
				"      notice, this list of conditions and the following disclaimer.\r\n" + 
				"\r\n" + 
				"    * Redistributions in binary form must reproduce the above copyright\r\n" + 
				"      notice, this list of conditions and the following disclaimer in the\r\n" + 
				"      documentation and/or other materials provided with the distribution.\r\n" + 
				"\r\n" + 
				"    * Neither the names of the copyright holders nor the names of the\r\n" + 
				"      University of New South Wales and the University of Waikato\r\n" + 
				"      and its contributors may be used to endorse or promote products derived\r\n" + 
				"      from this software without specific prior written permission.\r\n" + 
				"\r\n" + 
				"THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY\r\n" + 
				"EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\r\n" + 
				"WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\r\n" + 
				"DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY\r\n" + 
				"DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\r\n" + 
				"(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\r\n" + 
				"LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\r\n" + 
				"ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\r\n" + 
				"(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\r\n" + 
				"SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"The initial commit of package \"org.apache.commons.math3.ml.neuralnet\" is\r\n" + 
				"an adapted version of code developed in the context of the Data Processing\r\n" + 
				"and Analysis Consortium (DPAC) of the \"Gaia\" project of the European Space\r\n" + 
				"Agency (ESA).\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"The initial commit of the class \"org.apache.commons.math3.special.BesselJ\" is\r\n" + 
				"an adapted version of code translated from the netlib Fortran program, rjbesl\r\n" + 
				"http://www.netlib.org/specfun/rjbesl by R.J. Cody at Argonne National\r\n" + 
				"Laboratory (USA).  There is no license or copyright statement included with the\r\n" + 
				"original Fortran sources.\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"The BracketFinder (package org.apache.commons.math3.optimization.univariate)\r\n" + 
				"and PowellOptimizer (package org.apache.commons.math3.optimization.general)\r\n" + 
				"classes are based on the Python code in module \"optimize.py\" (version 0.5)\r\n" + 
				"developed by Travis E. Oliphant for the SciPy library (http://www.scipy.org/)\r\n" + 
				"Copyright © 2003-2009 SciPy Developers.\r\n" + 
				"\r\n" + 
				"SciPy license\r\n" + 
				"Copyright © 2001, 2002 Enthought, Inc.\r\n" + 
				"All rights reserved.\r\n" + 
				"\r\n" + 
				"Copyright © 2003-2013 SciPy Developers.\r\n" + 
				"All rights reserved.\r\n" + 
				"\r\n" + 
				"Redistribution and use in source and binary forms, with or without\r\n" + 
				"modification, are permitted provided that the following conditions are met:\r\n" + 
				"\r\n" + 
				"    * Redistributions of source code must retain the above copyright\r\n" + 
				"      notice, this list of conditions and the following disclaimer.\r\n" + 
				"\r\n" + 
				"    * Redistributions in binary form must reproduce the above copyright\r\n" + 
				"      notice, this list of conditions and the following disclaimer in the\r\n" + 
				"      documentation and/or other materials provided with the distribution.\r\n" + 
				"\r\n" + 
				"    * Neither the name of Enthought nor the names of the SciPy Developers may\r\n" + 
				"      be used to endorse or promote products derived from this software without\r\n" + 
				"      specific prior written permission.\r\n" + 
				"\r\n" + 
				"THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS�? AND ANY\r\n" + 
				"EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\r\n" + 
				"WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\r\n" + 
				"DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY\r\n" + 
				"DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\r\n" + 
				"(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\r\n" + 
				"LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\r\n" + 
				"ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\r\n" + 
				"(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\r\n" + 
				"SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\r\n" + 
				"===============================================================================\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"----\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"The following software may be included in this product: Gson\r\n" + 
				"This software contains the following license and notice below:\r\n" + 
				"\r\n" + 
				"Copyright 2008 Google Inc.\r\n" + 
				"\r\n" + 
				"Licensed under the Apache License, Version 2.0 (the \"License\");\r\n" + 
				"you may not use this file except in compliance with the License.\r\n" + 
				"You may obtain a copy of the License at\r\n" + 
				"\r\n" + 
				"    http://www.apache.org/licenses/LICENSE-2.0\r\n" + 
				"\r\n" + 
				"Unless required by applicable law or agreed to in writing, software\r\n" + 
				"distributed under the License is distributed on an \"AS IS\" BASIS,\r\n" + 
				"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\r\n" + 
				"See the License for the specific language governing permissions and\r\n" + 
				"limitations under the License.");
		
	}
	
	
}
