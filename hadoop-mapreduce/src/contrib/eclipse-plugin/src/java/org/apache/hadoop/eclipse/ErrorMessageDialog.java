begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jface
operator|.
name|dialogs
operator|.
name|MessageDialog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|swt
operator|.
name|widgets
operator|.
name|Display
import|;
end_import

begin_comment
comment|/**  * Error dialog helper  */
end_comment

begin_class
DECL|class|ErrorMessageDialog
specifier|public
class|class
name|ErrorMessageDialog
block|{
DECL|method|display (final String title, final String message)
specifier|public
specifier|static
name|void
name|display
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
block|{
name|Display
operator|.
name|getDefault
argument_list|()
operator|.
name|syncExec
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MessageDialog
operator|.
name|openError
argument_list|(
name|Display
operator|.
name|getDefault
argument_list|()
operator|.
name|getActiveShell
argument_list|()
argument_list|,
name|title
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|display (Exception e)
specifier|public
specifier|static
name|void
name|display
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|display
argument_list|(
literal|"An exception has occured!"
argument_list|,
literal|"Exception description:\n"
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

