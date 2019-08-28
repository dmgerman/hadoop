begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|ozShell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|JsonUtils
import|;
end_import

begin_comment
comment|/**  * Utility to print out response object in human readable form.  */
end_comment

begin_class
DECL|class|ObjectPrinter
specifier|public
specifier|final
class|class
name|ObjectPrinter
block|{
DECL|method|ObjectPrinter ()
specifier|private
name|ObjectPrinter
parameter_list|()
block|{   }
DECL|method|getObjectAsJson (Object o)
specifier|public
specifier|static
name|String
name|getObjectAsJson
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|JsonUtils
operator|.
name|toJsonStringWithDefaultPrettyPrinter
argument_list|(
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|o
argument_list|)
argument_list|)
return|;
block|}
DECL|method|printObjectAsJson (Object o)
specifier|public
specifier|static
name|void
name|printObjectAsJson
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getObjectAsJson
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

