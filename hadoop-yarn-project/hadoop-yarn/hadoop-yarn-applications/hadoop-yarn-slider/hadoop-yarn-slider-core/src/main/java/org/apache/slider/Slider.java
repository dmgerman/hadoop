begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider
package|package
name|org
operator|.
name|apache
operator|.
name|slider
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|client
operator|.
name|SliderClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|main
operator|.
name|ServiceLauncher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This is just the entry point class  */
end_comment

begin_class
DECL|class|Slider
specifier|public
class|class
name|Slider
extends|extends
name|SliderClient
block|{
DECL|field|SERVICE_CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_CLASSNAME
init|=
literal|"org.apache.slider.Slider"
decl_stmt|;
comment|/**    * This is the main entry point for the service launcher.    * @param args command line arguments.    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|//turn the args to a list
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
decl_stmt|;
comment|//create a new list, as the ArrayList type doesn't push() on an insert
name|List
argument_list|<
name|String
argument_list|>
name|extendedArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|argsList
argument_list|)
decl_stmt|;
comment|//insert the service name
name|extendedArgs
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|SERVICE_CLASSNAME
argument_list|)
expr_stmt|;
comment|//now have the service launcher do its work
name|ServiceLauncher
operator|.
name|serviceMain
argument_list|(
name|extendedArgs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

