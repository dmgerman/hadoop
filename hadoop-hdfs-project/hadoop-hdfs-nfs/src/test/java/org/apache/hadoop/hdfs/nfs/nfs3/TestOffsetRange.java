begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

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
name|io
operator|.
name|IOException
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
DECL|class|TestOffsetRange
specifier|public
class|class
name|TestOffsetRange
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testConstructor1 ()
specifier|public
name|void
name|testConstructor1
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|OffsetRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testConstructor2 ()
specifier|public
name|void
name|testConstructor2
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|OffsetRange
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testConstructor3 ()
specifier|public
name|void
name|testConstructor3
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|OffsetRange
argument_list|(
operator|-
literal|3
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testConstructor4 ()
specifier|public
name|void
name|testConstructor4
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|OffsetRange
argument_list|(
operator|-
literal|3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompare ()
specifier|public
name|void
name|testCompare
parameter_list|()
throws|throws
name|IOException
block|{
name|OffsetRange
name|r1
init|=
operator|new
name|OffsetRange
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|OffsetRange
name|r2
init|=
operator|new
name|OffsetRange
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|OffsetRange
name|r3
init|=
operator|new
name|OffsetRange
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|OffsetRange
name|r4
init|=
operator|new
name|OffsetRange
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r2
operator|.
name|compareTo
argument_list|(
name|r3
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r2
operator|.
name|compareTo
argument_list|(
name|r1
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r2
operator|.
name|compareTo
argument_list|(
name|r4
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

