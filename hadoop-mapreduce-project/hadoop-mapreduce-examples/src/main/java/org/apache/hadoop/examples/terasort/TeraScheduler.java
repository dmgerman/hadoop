begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.terasort
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|terasort
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputSplit
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|FileSplit
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
name|mapreduce
operator|.
name|server
operator|.
name|tasktracker
operator|.
name|TTConfig
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_class
DECL|class|TeraScheduler
class|class
name|TeraScheduler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TeraScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|splits
specifier|private
name|Split
index|[]
name|splits
decl_stmt|;
DECL|field|hosts
specifier|private
name|List
argument_list|<
name|Host
argument_list|>
name|hosts
init|=
operator|new
name|ArrayList
argument_list|<
name|Host
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|slotsPerHost
specifier|private
name|int
name|slotsPerHost
decl_stmt|;
DECL|field|remainingSplits
specifier|private
name|int
name|remainingSplits
init|=
literal|0
decl_stmt|;
DECL|field|realSplits
specifier|private
name|FileSplit
index|[]
name|realSplits
init|=
literal|null
decl_stmt|;
DECL|class|Split
specifier|static
class|class
name|Split
block|{
DECL|field|filename
name|String
name|filename
decl_stmt|;
DECL|field|isAssigned
name|boolean
name|isAssigned
init|=
literal|false
decl_stmt|;
DECL|field|locations
name|List
argument_list|<
name|Host
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<
name|Host
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Split (String filename)
name|Split
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|this
operator|.
name|filename
operator|=
name|filename
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" on "
argument_list|)
expr_stmt|;
for|for
control|(
name|Host
name|host
range|:
name|locations
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|host
operator|.
name|hostname
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|Host
specifier|static
class|class
name|Host
block|{
DECL|field|hostname
name|String
name|hostname
decl_stmt|;
DECL|field|splits
name|List
argument_list|<
name|Split
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|Split
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Host (String hostname)
name|Host
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|splits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|readFile (String filename)
name|List
argument_list|<
name|String
argument_list|>
name|readFile
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
try|try
init|(
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|filename
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|TeraScheduler (String splitFilename, String nodeFilename)
specifier|public
name|TeraScheduler
parameter_list|(
name|String
name|splitFilename
parameter_list|,
name|String
name|nodeFilename
parameter_list|)
throws|throws
name|IOException
block|{
name|slotsPerHost
operator|=
literal|4
expr_stmt|;
comment|// get the hosts
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|hostIds
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|hostName
range|:
name|readFile
argument_list|(
name|nodeFilename
argument_list|)
control|)
block|{
name|Host
name|host
init|=
operator|new
name|Host
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
name|hosts
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|hostIds
operator|.
name|put
argument_list|(
name|hostName
argument_list|,
name|host
argument_list|)
expr_stmt|;
block|}
comment|// read the blocks
name|List
argument_list|<
name|String
argument_list|>
name|splitLines
init|=
name|readFile
argument_list|(
name|splitFilename
argument_list|)
decl_stmt|;
name|splits
operator|=
operator|new
name|Split
index|[
name|splitLines
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|remainingSplits
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|String
name|line
range|:
name|splitLines
control|)
block|{
name|StringTokenizer
name|itr
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|Split
name|newSplit
init|=
operator|new
name|Split
argument_list|(
name|itr
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
name|splits
index|[
name|remainingSplits
operator|++
index|]
operator|=
name|newSplit
expr_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|Host
name|host
init|=
name|hostIds
operator|.
name|get
argument_list|(
name|itr
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
name|newSplit
operator|.
name|locations
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|host
operator|.
name|splits
operator|.
name|add
argument_list|(
name|newSplit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|TeraScheduler (FileSplit[] realSplits, Configuration conf)
specifier|public
name|TeraScheduler
parameter_list|(
name|FileSplit
index|[]
name|realSplits
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|realSplits
operator|=
name|realSplits
expr_stmt|;
name|this
operator|.
name|slotsPerHost
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|TTConfig
operator|.
name|TT_MAP_SLOTS
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|hostTable
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
argument_list|()
decl_stmt|;
name|splits
operator|=
operator|new
name|Split
index|[
name|realSplits
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|FileSplit
name|realSplit
range|:
name|realSplits
control|)
block|{
name|Split
name|split
init|=
operator|new
name|Split
argument_list|(
name|realSplit
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|splits
index|[
name|remainingSplits
operator|++
index|]
operator|=
name|split
expr_stmt|;
for|for
control|(
name|String
name|hostname
range|:
name|realSplit
operator|.
name|getLocations
argument_list|()
control|)
block|{
name|Host
name|host
init|=
name|hostTable
operator|.
name|get
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
name|host
operator|=
operator|new
name|Host
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
name|hostTable
operator|.
name|put
argument_list|(
name|hostname
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|hosts
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
name|host
operator|.
name|splits
operator|.
name|add
argument_list|(
name|split
argument_list|)
expr_stmt|;
name|split
operator|.
name|locations
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|pickBestHost ()
name|Host
name|pickBestHost
parameter_list|()
block|{
name|Host
name|result
init|=
literal|null
decl_stmt|;
name|int
name|splits
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|Host
name|host
range|:
name|hosts
control|)
block|{
if|if
condition|(
name|host
operator|.
name|splits
operator|.
name|size
argument_list|()
operator|<
name|splits
condition|)
block|{
name|result
operator|=
name|host
expr_stmt|;
name|splits
operator|=
name|host
operator|.
name|splits
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|hosts
operator|.
name|remove
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"picking "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|pickBestSplits (Host host)
name|void
name|pickBestSplits
parameter_list|(
name|Host
name|host
parameter_list|)
block|{
name|int
name|tasksToPick
init|=
name|Math
operator|.
name|min
argument_list|(
name|slotsPerHost
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|remainingSplits
operator|/
name|hosts
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Split
index|[]
name|best
init|=
operator|new
name|Split
index|[
name|tasksToPick
index|]
decl_stmt|;
for|for
control|(
name|Split
name|cur
range|:
name|host
operator|.
name|splits
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"  examine: "
operator|+
name|cur
operator|.
name|filename
operator|+
literal|" "
operator|+
name|cur
operator|.
name|locations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|tasksToPick
operator|&&
name|best
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|best
index|[
name|i
index|]
operator|.
name|locations
operator|.
name|size
argument_list|()
operator|<=
name|cur
operator|.
name|locations
operator|.
name|size
argument_list|()
condition|)
block|{
name|i
operator|+=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
name|tasksToPick
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
name|tasksToPick
operator|-
literal|1
init|;
name|j
operator|>
name|i
condition|;
operator|--
name|j
control|)
block|{
name|best
index|[
name|j
index|]
operator|=
name|best
index|[
name|j
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|best
index|[
name|i
index|]
operator|=
name|cur
expr_stmt|;
block|}
block|}
comment|// for the chosen blocks, remove them from the other locations
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tasksToPick
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|best
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|" best: "
operator|+
name|best
index|[
name|i
index|]
operator|.
name|filename
argument_list|)
expr_stmt|;
for|for
control|(
name|Host
name|other
range|:
name|best
index|[
name|i
index|]
operator|.
name|locations
control|)
block|{
name|other
operator|.
name|splits
operator|.
name|remove
argument_list|(
name|best
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|best
index|[
name|i
index|]
operator|.
name|locations
operator|.
name|clear
argument_list|()
expr_stmt|;
name|best
index|[
name|i
index|]
operator|.
name|locations
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|best
index|[
name|i
index|]
operator|.
name|isAssigned
operator|=
literal|true
expr_stmt|;
name|remainingSplits
operator|-=
literal|1
expr_stmt|;
block|}
block|}
comment|// for the non-chosen blocks, remove this host
for|for
control|(
name|Split
name|cur
range|:
name|host
operator|.
name|splits
control|)
block|{
if|if
condition|(
operator|!
name|cur
operator|.
name|isAssigned
condition|)
block|{
name|cur
operator|.
name|locations
operator|.
name|remove
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|solve ()
name|void
name|solve
parameter_list|()
throws|throws
name|IOException
block|{
name|Host
name|host
init|=
name|pickBestHost
argument_list|()
decl_stmt|;
while|while
condition|(
name|host
operator|!=
literal|null
condition|)
block|{
name|pickBestSplits
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|host
operator|=
name|pickBestHost
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Solve the schedule and modify the FileSplit array to reflect the new    * schedule. It will move placed splits to front and unplacable splits    * to the end.    * @return a new list of FileSplits that are modified to have the    *    best host as the only host.    * @throws IOException    */
DECL|method|getNewFileSplits ()
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getNewFileSplits
parameter_list|()
throws|throws
name|IOException
block|{
name|solve
argument_list|()
expr_stmt|;
name|FileSplit
index|[]
name|result
init|=
operator|new
name|FileSplit
index|[
name|realSplits
operator|.
name|length
index|]
decl_stmt|;
name|int
name|left
init|=
literal|0
decl_stmt|;
name|int
name|right
init|=
name|realSplits
operator|.
name|length
operator|-
literal|1
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
name|splits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|splits
index|[
name|i
index|]
operator|.
name|isAssigned
condition|)
block|{
comment|// copy the split and fix up the locations
name|String
index|[]
name|newLocations
init|=
block|{
name|splits
index|[
name|i
index|]
operator|.
name|locations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|hostname
block|}
decl_stmt|;
name|realSplits
index|[
name|i
index|]
operator|=
operator|new
name|FileSplit
argument_list|(
name|realSplits
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
argument_list|,
name|realSplits
index|[
name|i
index|]
operator|.
name|getStart
argument_list|()
argument_list|,
name|realSplits
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
name|newLocations
argument_list|)
expr_stmt|;
name|result
index|[
name|left
operator|++
index|]
operator|=
name|realSplits
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|result
index|[
name|right
operator|--
index|]
operator|=
name|realSplits
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|InputSplit
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FileSplit
name|fs
range|:
name|result
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
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
throws|throws
name|IOException
block|{
name|TeraScheduler
name|problem
init|=
operator|new
name|TeraScheduler
argument_list|(
literal|"block-loc.txt"
argument_list|,
literal|"nodes"
argument_list|)
decl_stmt|;
for|for
control|(
name|Host
name|host
range|:
name|problem
operator|.
name|hosts
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"starting solve"
argument_list|)
expr_stmt|;
name|problem
operator|.
name|solve
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Split
argument_list|>
name|leftOvers
init|=
operator|new
name|ArrayList
argument_list|<
name|Split
argument_list|>
argument_list|()
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
name|problem
operator|.
name|splits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|problem
operator|.
name|splits
index|[
name|i
index|]
operator|.
name|isAssigned
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sched: "
operator|+
name|problem
operator|.
name|splits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|leftOvers
operator|.
name|add
argument_list|(
name|problem
operator|.
name|splits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Split
name|cur
range|:
name|leftOvers
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"left: "
operator|+
name|cur
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"left over: "
operator|+
name|leftOvers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

