begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.utils
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|utils
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
name|service
operator|.
name|conf
operator|.
name|SliderKeys
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
name|common
operator|.
name|SliderXMLConfKeysForTesting
import|;
end_import

begin_comment
comment|/**  * Keys shared across tests.  */
end_comment

begin_interface
DECL|interface|KeysForTests
specifier|public
interface|interface
name|KeysForTests
extends|extends
name|SliderKeys
extends|,
name|SliderXMLConfKeysForTesting
block|{
comment|/**    * Username for all clusters, ZK, etc.    */
DECL|field|USERNAME
name|String
name|USERNAME
init|=
literal|"bigdataborat"
decl_stmt|;
DECL|field|WAIT_TIME
name|int
name|WAIT_TIME
init|=
literal|120
decl_stmt|;
DECL|field|WAIT_TIME_ARG
name|String
name|WAIT_TIME_ARG
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|WAIT_TIME
argument_list|)
decl_stmt|;
DECL|field|SLIDER_TEST_XML
name|String
name|SLIDER_TEST_XML
init|=
literal|"slider-test.xml"
decl_stmt|;
block|}
end_interface

end_unit

