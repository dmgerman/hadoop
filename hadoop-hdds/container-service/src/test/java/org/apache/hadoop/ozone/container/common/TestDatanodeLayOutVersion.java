begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests DatanodeLayOutVersion.  */
end_comment

begin_class
DECL|class|TestDatanodeLayOutVersion
specifier|public
class|class
name|TestDatanodeLayOutVersion
block|{
annotation|@
name|Test
DECL|method|testDatanodeLayOutVersion ()
specifier|public
name|void
name|testDatanodeLayOutVersion
parameter_list|()
block|{
comment|// Check Latest Version and description
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DataNodeLayoutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"HDDS Datanode LayOut Version 1"
argument_list|,
name|DataNodeLayoutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DataNodeLayoutVersion
operator|.
name|getAllVersions
argument_list|()
operator|.
name|length
argument_list|,
name|DataNodeLayoutVersion
operator|.
name|getAllVersions
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

