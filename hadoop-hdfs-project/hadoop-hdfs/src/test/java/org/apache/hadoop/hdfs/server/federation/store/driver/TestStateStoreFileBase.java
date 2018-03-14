begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.driver
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|impl
operator|.
name|StateStoreFileBaseImpl
operator|.
name|isOldTempRecord
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|util
operator|.
name|Time
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
comment|/**  * Tests for the State Store file based implementation.  */
end_comment

begin_class
DECL|class|TestStateStoreFileBase
specifier|public
class|class
name|TestStateStoreFileBase
block|{
annotation|@
name|Test
DECL|method|testTempOld ()
specifier|public
name|void
name|testTempOld
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|isOldTempRecord
argument_list|(
literal|"test.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isOldTempRecord
argument_list|(
literal|"testfolder/test.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|tnow
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|String
name|tmpFile1
init|=
literal|"test."
operator|+
name|tnow
operator|+
literal|".tmp"
decl_stmt|;
name|assertFalse
argument_list|(
name|isOldTempRecord
argument_list|(
name|tmpFile1
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|told
init|=
name|Time
operator|.
name|now
argument_list|()
operator|-
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|tmpFile2
init|=
literal|"test."
operator|+
name|told
operator|+
literal|".tmp"
decl_stmt|;
name|assertTrue
argument_list|(
name|isOldTempRecord
argument_list|(
name|tmpFile2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

