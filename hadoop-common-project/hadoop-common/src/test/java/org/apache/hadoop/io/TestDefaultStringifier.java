begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|Configuration
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
name|assertEquals
import|;
end_import

begin_class
DECL|class|TestDefaultStringifier
specifier|public
class|class
name|TestDefaultStringifier
block|{
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDefaultStringifier
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|alphabet
specifier|private
name|char
index|[]
name|alphabet
init|=
literal|"abcdefghijklmnopqrstuvwxyz"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testWithWritable ()
specifier|public
name|void
name|testWithWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"io.serializations"
argument_list|,
literal|"org.apache.hadoop.io.serializer.WritableSerialization"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing DefaultStringifier with Text"
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|//test with a Text
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|//generate a random string
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|strLen
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|40
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|strLen
condition|;
name|j
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|alphabet
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|alphabet
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|DefaultStringifier
argument_list|<
name|Text
argument_list|>
name|stringifier
init|=
operator|new
name|DefaultStringifier
argument_list|<
name|Text
argument_list|>
argument_list|(
name|conf
argument_list|,
name|Text
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|stringifier
operator|.
name|toString
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|Text
name|claimedText
init|=
name|stringifier
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Object: "
operator|+
name|text
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"String representation of the object: "
operator|+
name|str
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|text
argument_list|,
name|claimedText
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWithJavaSerialization ()
specifier|public
name|void
name|testWithJavaSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"io.serializations"
argument_list|,
literal|"org.apache.hadoop.io.serializer.JavaSerialization"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing DefaultStringifier with Serializable Integer"
argument_list|)
expr_stmt|;
comment|//Integer implements Serializable
name|Integer
name|testInt
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|DefaultStringifier
argument_list|<
name|Integer
argument_list|>
name|stringifier
init|=
operator|new
name|DefaultStringifier
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|conf
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|stringifier
operator|.
name|toString
argument_list|(
name|testInt
argument_list|)
decl_stmt|;
name|Integer
name|claimedInt
init|=
name|stringifier
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"String representation of the object: "
operator|+
name|str
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testInt
argument_list|,
name|claimedInt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStoreLoad ()
specifier|public
name|void
name|testStoreLoad
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing DefaultStringifier#store() and #load()"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"io.serializations"
argument_list|,
literal|"org.apache.hadoop.io.serializer.WritableSerialization"
argument_list|)
expr_stmt|;
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
literal|"uninteresting test string"
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"test.defaultstringifier.key1"
decl_stmt|;
name|DefaultStringifier
operator|.
name|store
argument_list|(
name|conf
argument_list|,
name|text
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
name|Text
name|claimedText
init|=
name|DefaultStringifier
operator|.
name|load
argument_list|(
name|conf
argument_list|,
name|keyName
argument_list|,
name|Text
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DefaultStringifier#load() or #store() might be flawed"
argument_list|,
name|text
argument_list|,
name|claimedText
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStoreLoadArray ()
specifier|public
name|void
name|testStoreLoadArray
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing DefaultStringifier#storeArray() and #loadArray()"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"io.serializations"
argument_list|,
literal|"org.apache.hadoop.io.serializer.JavaSerialization"
argument_list|)
expr_stmt|;
name|String
name|keyName
init|=
literal|"test.defaultstringifier.key2"
decl_stmt|;
name|Integer
index|[]
name|array
init|=
operator|new
name|Integer
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
name|DefaultStringifier
operator|.
name|storeArray
argument_list|(
name|conf
argument_list|,
name|array
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
name|Integer
index|[]
name|claimedArray
init|=
name|DefaultStringifier
operator|.
expr|<
name|Integer
operator|>
name|loadArray
argument_list|(
name|conf
argument_list|,
name|keyName
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"two arrays are not equal"
argument_list|,
name|array
index|[
name|i
index|]
argument_list|,
name|claimedArray
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

