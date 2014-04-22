begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
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
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|NfsTime
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
name|oncrpc
operator|.
name|XDR
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

begin_class
DECL|class|TestNfsTime
specifier|public
class|class
name|TestNfsTime
block|{
annotation|@
name|Test
DECL|method|testConstructor ()
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
name|NfsTime
name|nfstime
init|=
operator|new
name|NfsTime
argument_list|(
literal|1001
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nfstime
operator|.
name|getSeconds
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000000
argument_list|,
name|nfstime
operator|.
name|getNseconds
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerializeDeserialize ()
specifier|public
name|void
name|testSerializeDeserialize
parameter_list|()
block|{
comment|// Serialize NfsTime
name|NfsTime
name|t1
init|=
operator|new
name|NfsTime
argument_list|(
literal|1001
argument_list|)
decl_stmt|;
name|XDR
name|xdr
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|t1
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
comment|// Deserialize it back
name|NfsTime
name|t2
init|=
name|NfsTime
operator|.
name|deserialize
argument_list|(
name|xdr
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|)
decl_stmt|;
comment|// Ensure the NfsTimes are equal
name|Assert
operator|.
name|assertEquals
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

