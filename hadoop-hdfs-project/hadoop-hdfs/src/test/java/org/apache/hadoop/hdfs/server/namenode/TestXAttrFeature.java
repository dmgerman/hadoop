begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|XAttr
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
name|hdfs
operator|.
name|XAttrHelper
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestXAttrFeature
specifier|public
class|class
name|TestXAttrFeature
block|{
DECL|field|name1
specifier|static
specifier|final
name|String
name|name1
init|=
literal|"system.a1"
decl_stmt|;
DECL|field|value1
specifier|static
specifier|final
name|byte
index|[]
name|value1
init|=
block|{
literal|0x31
block|,
literal|0x32
block|,
literal|0x33
block|}
decl_stmt|;
DECL|field|name2
specifier|static
specifier|final
name|String
name|name2
init|=
literal|"security.a2"
decl_stmt|;
DECL|field|value2
specifier|static
specifier|final
name|byte
index|[]
name|value2
init|=
block|{
literal|0x37
block|,
literal|0x38
block|,
literal|0x39
block|}
decl_stmt|;
DECL|field|name3
specifier|static
specifier|final
name|String
name|name3
init|=
literal|"trusted.a3"
decl_stmt|;
DECL|field|name4
specifier|static
specifier|final
name|String
name|name4
init|=
literal|"user.a4"
decl_stmt|;
DECL|field|value4
specifier|static
specifier|final
name|byte
index|[]
name|value4
init|=
block|{
literal|0x01
block|,
literal|0x02
block|,
literal|0x03
block|}
decl_stmt|;
DECL|field|name5
specifier|static
specifier|final
name|String
name|name5
init|=
literal|"user.a5"
decl_stmt|;
DECL|field|value5
specifier|static
specifier|final
name|byte
index|[]
name|value5
init|=
name|randomBytes
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
DECL|field|name6
specifier|static
specifier|final
name|String
name|name6
init|=
literal|"user.a6"
decl_stmt|;
DECL|field|value6
specifier|static
specifier|final
name|byte
index|[]
name|value6
init|=
name|randomBytes
argument_list|(
literal|1800
argument_list|)
decl_stmt|;
DECL|field|name7
specifier|static
specifier|final
name|String
name|name7
init|=
literal|"raw.a7"
decl_stmt|;
DECL|field|value7
specifier|static
specifier|final
name|byte
index|[]
name|value7
init|=
block|{
literal|0x011
block|,
literal|0x012
block|,
literal|0x013
block|}
decl_stmt|;
DECL|field|name8
specifier|static
specifier|final
name|String
name|name8
init|=
literal|"user.a8"
decl_stmt|;
DECL|method|randomBytes (int len)
specifier|static
name|byte
index|[]
name|randomBytes
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
annotation|@
name|Test
DECL|method|testXAttrFeature ()
specifier|public
name|void
name|testXAttrFeature
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|XAttrFeature
name|feature
init|=
operator|new
name|XAttrFeature
argument_list|(
name|xAttrs
argument_list|)
decl_stmt|;
comment|// no XAttrs in the feature
name|assertTrue
argument_list|(
name|feature
operator|.
name|getXAttrs
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// one XAttr in the feature
name|XAttr
name|a1
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name1
argument_list|,
name|value1
argument_list|)
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|feature
operator|=
operator|new
name|XAttrFeature
argument_list|(
name|xAttrs
argument_list|)
expr_stmt|;
name|XAttr
name|r1
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|r1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|feature
operator|.
name|getXAttrs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// more XAttrs in the feature
name|XAttr
name|a2
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name2
argument_list|,
name|value2
argument_list|)
decl_stmt|;
name|XAttr
name|a3
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name3
argument_list|)
decl_stmt|;
name|XAttr
name|a4
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name4
argument_list|,
name|value4
argument_list|)
decl_stmt|;
name|XAttr
name|a5
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name5
argument_list|,
name|value5
argument_list|)
decl_stmt|;
name|XAttr
name|a6
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name6
argument_list|,
name|value6
argument_list|)
decl_stmt|;
name|XAttr
name|a7
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|name7
argument_list|,
name|value7
argument_list|)
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a3
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a4
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a5
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a6
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|a7
argument_list|)
expr_stmt|;
name|feature
operator|=
operator|new
name|XAttrFeature
argument_list|(
name|xAttrs
argument_list|)
expr_stmt|;
name|XAttr
name|r2
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a2
operator|.
name|equals
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
name|XAttr
name|r3
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a3
operator|.
name|equals
argument_list|(
name|r3
argument_list|)
argument_list|)
expr_stmt|;
name|XAttr
name|r4
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name4
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a4
operator|.
name|equals
argument_list|(
name|r4
argument_list|)
argument_list|)
expr_stmt|;
name|XAttr
name|r5
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a5
operator|.
name|equals
argument_list|(
name|r5
argument_list|)
argument_list|)
expr_stmt|;
name|XAttr
name|r6
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name6
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a6
operator|.
name|equals
argument_list|(
name|r6
argument_list|)
argument_list|)
expr_stmt|;
name|XAttr
name|r7
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name7
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a7
operator|.
name|equals
argument_list|(
name|r7
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|rs
init|=
name|feature
operator|.
name|getXAttrs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|rs
operator|.
name|size
argument_list|()
argument_list|,
name|xAttrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|xAttrs
operator|.
name|contains
argument_list|(
name|rs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// get non-exist XAttr in the feature
name|XAttr
name|r8
init|=
name|feature
operator|.
name|getXAttr
argument_list|(
name|name8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r8
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

