begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.vaidya
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|vaidya
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|vaidya
operator|.
name|util
operator|.
name|XMLUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_comment
comment|/**  * This is a base driver class for job diagnostics. Various specialty drivers that  * tests specific aspects of job problems e.g. PostExPerformanceDiagnoser extends the  * this base class.  *  */
end_comment

begin_class
DECL|class|JobDiagnoser
specifier|public
class|class
name|JobDiagnoser
block|{
comment|/*    * XML document containing report elements, one for each rule tested    */
DECL|field|_report
specifier|private
name|Document
name|_report
decl_stmt|;
comment|/*    * @report : returns report document    */
DECL|method|getReport ()
specifier|public
name|Document
name|getReport
parameter_list|()
block|{
return|return
name|this
operator|.
name|_report
return|;
block|}
comment|/**    * Constructor. It initializes the report document.    */
DECL|method|JobDiagnoser ()
specifier|public
name|JobDiagnoser
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*      * Initialize the report document, make it ready to add the child report elements       */
name|DocumentBuilder
name|builder
init|=
literal|null
decl_stmt|;
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|_report
operator|=
name|builder
operator|.
name|newDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Insert Root Element
name|Element
name|root
init|=
operator|(
name|Element
operator|)
name|this
operator|.
name|_report
operator|.
name|createElement
argument_list|(
literal|"PostExPerformanceDiagnosticReport"
argument_list|)
decl_stmt|;
name|this
operator|.
name|_report
operator|.
name|appendChild
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
comment|/*    * Print the report document to console    */
DECL|method|printReport ()
specifier|public
name|void
name|printReport
parameter_list|()
block|{
name|XMLUtils
operator|.
name|printDOM
argument_list|(
name|this
operator|.
name|_report
argument_list|)
expr_stmt|;
block|}
comment|/*    * Save the report document the specified report file    * @param reportfile : path of report file.     */
DECL|method|saveReport (String filename)
specifier|public
name|void
name|saveReport
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|XMLUtils
operator|.
name|writeXmlToFile
argument_list|(
name|filename
argument_list|,
name|this
operator|.
name|_report
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

