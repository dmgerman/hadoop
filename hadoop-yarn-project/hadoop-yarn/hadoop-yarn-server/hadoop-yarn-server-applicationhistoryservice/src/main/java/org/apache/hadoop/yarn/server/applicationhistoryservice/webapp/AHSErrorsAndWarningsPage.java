begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|applicationhistoryservice
operator|.
name|webapp
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
name|yarn
operator|.
name|server
operator|.
name|webapp
operator|.
name|ErrorsAndWarningsBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|SubView
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Class to display the Errors and Warnings page for the AHS.  */
end_comment

begin_class
DECL|class|AHSErrorsAndWarningsPage
specifier|public
class|class
name|AHSErrorsAndWarningsPage
extends|extends
name|AHSView
block|{
annotation|@
name|Override
DECL|method|content ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|ErrorsAndWarningsBlock
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|preHead (Page.HTML<__> html)
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|__
argument_list|>
name|html
parameter_list|)
block|{
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|String
name|title
init|=
literal|"Errors and Warnings in the Application History Server"
decl_stmt|;
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|String
name|tableId
init|=
literal|"messages"
decl_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
name|tableId
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
name|tableId
argument_list|)
argument_list|,
name|tablesInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
name|tableId
argument_list|,
literal|".message {width:50em}"
argument_list|,
literal|".count {width:8em}"
argument_list|,
literal|".lasttime {width:16em}"
argument_list|)
expr_stmt|;
block|}
DECL|method|tablesInit ()
specifier|private
name|String
name|tablesInit
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", aoColumnDefs: ["
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"{'sType': 'string', 'aTargets': [ 0 ]}"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", {'sType': 'string', 'bSearchable': true, 'aTargets': [ 1 ]}"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", {'sType': 'numeric', 'bSearchable': false, 'aTargets': [ 2 ]}"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", {'sType': 'date', 'aTargets': [ 3 ] }]"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", aaSorting: [[3, 'desc']]}"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

