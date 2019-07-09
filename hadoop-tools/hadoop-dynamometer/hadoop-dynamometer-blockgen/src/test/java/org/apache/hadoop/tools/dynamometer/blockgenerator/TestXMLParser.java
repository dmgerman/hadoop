begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.blockgenerator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|blockgenerator
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/** Tests for {@link XMLParser}. */
end_comment

begin_class
DECL|class|TestXMLParser
specifier|public
class|class
name|TestXMLParser
block|{
comment|/**    * Testing whether {@link XMLParser} correctly parses an XML fsimage file into    * {@link BlockInfo}'s. Note that some files have multiple lines.    */
annotation|@
name|Test
DECL|method|testBlocksFromLine ()
specifier|public
name|void
name|testBlocksFromLine
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|lines
init|=
block|{
literal|"<INodeSection><lastInodeId>1"
operator|+
literal|"</lastInodeId><inode><id>2</id><type>FILE</type>"
operator|+
literal|"<name>fake-file</name>"
operator|+
literal|"<replication>3</replication><mtime>3</mtime>"
operator|+
literal|"<atime>4</atime>"
operator|+
literal|"<perferredBlockSize>5</perferredBlockSize>"
operator|+
literal|"<permission>hdfs:hdfs:rw-------</permission>"
operator|+
literal|"<blocks><block><id>6</id><genstamp>7</genstamp>"
operator|+
literal|"<numBytes>8</numBytes></block>"
operator|+
literal|"<block><id>9</id><genstamp>10</genstamp>"
operator|+
literal|"<numBytes>11</numBytes></block></inode>"
block|,
literal|"<inode><type>DIRECTORY</type></inode>"
block|,
literal|"<inode><type>FILE</type>"
block|,
literal|"<replication>12</replication>"
block|,
literal|"<blocks><block><id>13</id><genstamp>14</genstamp>"
operator|+
literal|"<numBytes>15</numBytes></block>"
block|,
literal|"</inode>"
block|,
literal|"</INodeSection>"
block|}
decl_stmt|;
name|short
name|replCount
init|=
literal|0
decl_stmt|;
comment|// This is ignored
name|Map
argument_list|<
name|BlockInfo
argument_list|,
name|Short
argument_list|>
name|expectedBlockCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedBlockCount
operator|.
name|put
argument_list|(
operator|new
name|BlockInfo
argument_list|(
literal|6
argument_list|,
literal|7
argument_list|,
literal|8
argument_list|,
name|replCount
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|expectedBlockCount
operator|.
name|put
argument_list|(
operator|new
name|BlockInfo
argument_list|(
literal|9
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|,
name|replCount
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|expectedBlockCount
operator|.
name|put
argument_list|(
operator|new
name|BlockInfo
argument_list|(
literal|13
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
name|replCount
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|12
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|BlockInfo
argument_list|,
name|Short
argument_list|>
name|actualBlockCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|XMLParser
name|parser
init|=
operator|new
name|XMLParser
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
for|for
control|(
name|BlockInfo
name|info
range|:
name|parser
operator|.
name|parseLine
argument_list|(
name|line
argument_list|)
control|)
block|{
name|actualBlockCount
operator|.
name|put
argument_list|(
name|info
argument_list|,
name|info
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|BlockInfo
argument_list|,
name|Short
argument_list|>
name|expect
range|:
name|expectedBlockCount
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|expect
operator|.
name|getValue
argument_list|()
argument_list|,
name|actualBlockCount
operator|.
name|get
argument_list|(
name|expect
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNonInodeSectionIgnored ()
specifier|public
name|void
name|testNonInodeSectionIgnored
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|lines
init|=
block|{
literal|"<INodeSection>"
block|,
literal|"</INodeSection>"
block|,
literal|"<OtherSection>"
block|,
literal|"<inode><id>1</id><type>FILE</type><name>fake-file</name>"
operator|+
literal|"<replication>1</replication>"
block|,
literal|"<blocks><block><id>2</id><genstamp>1</genstamp>"
operator|+
literal|"<numBytes>1</numBytes></block>"
block|,
literal|"</inode>"
block|,
literal|"<replication>3</replication>"
block|,
literal|"</OtherSection>"
block|}
decl_stmt|;
name|XMLParser
name|parser
init|=
operator|new
name|XMLParser
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
name|assertTrue
argument_list|(
operator|(
name|parser
operator|.
name|parseLine
argument_list|(
name|line
argument_list|)
operator|.
name|isEmpty
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

