begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|fs
operator|.
name|azure
operator|.
name|integration
operator|.
name|AzureTestConstants
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
name|fs
operator|.
name|azure
operator|.
name|integration
operator|.
name|AzureTestUtils
operator|.
name|assumeScaleTestsEnabled
import|;
end_import

begin_comment
comment|/**  * Integration tests at bigger scale; configurable as to  * size, off by default.  */
end_comment

begin_class
DECL|class|AbstractAbfsScaleTest
specifier|public
class|class
name|AbstractAbfsScaleTest
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractAbfsScaleTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getTestTimeoutMillis ()
specifier|protected
name|int
name|getTestTimeoutMillis
parameter_list|()
block|{
return|return
name|AzureTestConstants
operator|.
name|SCALE_TEST_TIMEOUT_MILLIS
return|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scale test operation count = {}"
argument_list|,
name|getOperationCount
argument_list|()
argument_list|)
expr_stmt|;
name|assumeScaleTestsEnabled
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getOperationCount ()
specifier|protected
name|long
name|getOperationCount
parameter_list|()
block|{
return|return
name|getConfiguration
argument_list|()
operator|.
name|getLong
argument_list|(
name|AzureTestConstants
operator|.
name|KEY_OPERATION_COUNT
argument_list|,
name|AzureTestConstants
operator|.
name|DEFAULT_OPERATION_COUNT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

