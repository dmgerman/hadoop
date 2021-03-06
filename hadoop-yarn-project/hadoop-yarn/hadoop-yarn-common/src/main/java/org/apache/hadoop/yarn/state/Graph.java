begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.state
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|state
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

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
name|HashSet
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
name|Set
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
name|text
operator|.
name|StringEscapeUtils
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|Graph
specifier|public
class|class
name|Graph
block|{
DECL|class|Edge
specifier|public
class|class
name|Edge
block|{
DECL|field|from
name|Node
name|from
decl_stmt|;
DECL|field|to
name|Node
name|to
decl_stmt|;
DECL|field|label
name|String
name|label
decl_stmt|;
DECL|method|Edge (Node from, Node to, String info)
specifier|public
name|Edge
parameter_list|(
name|Node
name|from
parameter_list|,
name|Node
name|to
parameter_list|,
name|String
name|info
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|label
operator|=
name|info
expr_stmt|;
block|}
DECL|method|sameAs (Edge rhs)
specifier|public
name|boolean
name|sameAs
parameter_list|(
name|Edge
name|rhs
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|from
operator|==
name|rhs
operator|.
name|from
operator|&&
name|this
operator|.
name|to
operator|==
name|rhs
operator|.
name|to
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|combine (Edge rhs)
specifier|public
name|Edge
name|combine
parameter_list|(
name|Edge
name|rhs
parameter_list|)
block|{
name|String
name|newlabel
init|=
name|this
operator|.
name|label
operator|+
literal|","
operator|+
name|rhs
operator|.
name|label
decl_stmt|;
return|return
operator|new
name|Edge
argument_list|(
name|this
operator|.
name|from
argument_list|,
name|this
operator|.
name|to
argument_list|,
name|newlabel
argument_list|)
return|;
block|}
block|}
DECL|class|Node
specifier|public
class|class
name|Node
block|{
DECL|field|parent
name|Graph
name|parent
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|ins
name|List
argument_list|<
name|Edge
argument_list|>
name|ins
decl_stmt|;
DECL|field|outs
name|List
argument_list|<
name|Edge
argument_list|>
name|outs
decl_stmt|;
DECL|method|Node (String id)
specifier|public
name|Node
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|Graph
operator|.
name|this
expr_stmt|;
name|this
operator|.
name|ins
operator|=
operator|new
name|ArrayList
argument_list|<
name|Graph
operator|.
name|Edge
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|outs
operator|=
operator|new
name|ArrayList
argument_list|<
name|Graph
operator|.
name|Edge
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|getParent ()
specifier|public
name|Graph
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|addEdge (Node to, String info)
specifier|public
name|Node
name|addEdge
parameter_list|(
name|Node
name|to
parameter_list|,
name|String
name|info
parameter_list|)
block|{
name|Edge
name|e
init|=
operator|new
name|Edge
argument_list|(
name|this
argument_list|,
name|to
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|outs
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|to
operator|.
name|ins
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getUniqueId ()
specifier|public
name|String
name|getUniqueId
parameter_list|()
block|{
return|return
name|Graph
operator|.
name|this
operator|.
name|name
operator|+
literal|"."
operator|+
name|id
return|;
block|}
block|}
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|parent
specifier|private
name|Graph
name|parent
decl_stmt|;
DECL|field|nodes
specifier|private
name|Set
argument_list|<
name|Graph
operator|.
name|Node
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<
name|Graph
operator|.
name|Node
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|subgraphs
specifier|private
name|Set
argument_list|<
name|Graph
argument_list|>
name|subgraphs
init|=
operator|new
name|HashSet
argument_list|<
name|Graph
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Graph (String name, Graph parent)
specifier|public
name|Graph
parameter_list|(
name|String
name|name
parameter_list|,
name|Graph
name|parent
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
DECL|method|Graph (String name)
specifier|public
name|Graph
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Graph ()
specifier|public
name|Graph
parameter_list|()
block|{
name|this
argument_list|(
literal|"graph"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getParent ()
specifier|public
name|Graph
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|newNode (String id)
specifier|private
name|Node
name|newNode
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|Node
name|ret
init|=
operator|new
name|Node
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|getNode (String id)
specifier|public
name|Node
name|getNode
parameter_list|(
name|String
name|id
parameter_list|)
block|{
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|node
operator|.
name|id
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|node
return|;
block|}
block|}
return|return
name|newNode
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|newSubGraph (String name)
specifier|public
name|Graph
name|newSubGraph
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Graph
name|ret
init|=
operator|new
name|Graph
argument_list|(
name|name
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|subgraphs
operator|.
name|add
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|addSubGraph (Graph graph)
specifier|public
name|void
name|addSubGraph
parameter_list|(
name|Graph
name|graph
parameter_list|)
block|{
name|subgraphs
operator|.
name|add
argument_list|(
name|graph
argument_list|)
expr_stmt|;
name|graph
operator|.
name|parent
operator|=
name|this
expr_stmt|;
block|}
DECL|method|wrapSafeString (String label)
specifier|private
specifier|static
name|String
name|wrapSafeString
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|label
operator|.
name|length
argument_list|()
operator|>
literal|14
condition|)
block|{
name|label
operator|=
name|label
operator|.
name|replaceAll
argument_list|(
literal|","
argument_list|,
literal|",\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|label
operator|=
literal|"\""
operator|+
name|StringEscapeUtils
operator|.
name|escapeJava
argument_list|(
name|label
argument_list|)
operator|+
literal|"\""
expr_stmt|;
return|return
name|label
return|;
block|}
DECL|method|generateGraphViz (String indent)
specifier|public
name|String
name|generateGraphViz
parameter_list|(
name|String
name|indent
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|parent
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"digraph "
operator|+
name|name
operator|+
literal|" {\n"
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"graph [ label=%s, fontsize=24, fontname=Helvetica];%n"
argument_list|,
name|wrapSafeString
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"node [fontsize=12, fontname=Helvetica];\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"edge [fontsize=9, fontcolor=blue, fontname=Arial];\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"subgraph cluster_"
operator|+
name|name
operator|+
literal|" {\nlabel=\""
operator|+
name|name
operator|+
literal|"\"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Graph
name|g
range|:
name|subgraphs
control|)
block|{
name|String
name|ginfo
init|=
name|g
operator|.
name|generateGraphViz
argument_list|(
name|indent
operator|+
literal|"  "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ginfo
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Node
name|n
range|:
name|nodes
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s%s [ label = %s ];%n"
argument_list|,
name|indent
argument_list|,
name|wrapSafeString
argument_list|(
name|n
operator|.
name|getUniqueId
argument_list|()
argument_list|)
argument_list|,
name|n
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Edge
argument_list|>
name|combinedOuts
init|=
name|combineEdges
argument_list|(
name|n
operator|.
name|outs
argument_list|)
decl_stmt|;
for|for
control|(
name|Edge
name|e
range|:
name|combinedOuts
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s%s -> %s [ label = %s ];%n"
argument_list|,
name|indent
argument_list|,
name|wrapSafeString
argument_list|(
name|e
operator|.
name|from
operator|.
name|getUniqueId
argument_list|()
argument_list|)
argument_list|,
name|wrapSafeString
argument_list|(
name|e
operator|.
name|to
operator|.
name|getUniqueId
argument_list|()
argument_list|)
argument_list|,
name|wrapSafeString
argument_list|(
name|e
operator|.
name|label
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|generateGraphViz ()
specifier|public
name|String
name|generateGraphViz
parameter_list|()
block|{
return|return
name|generateGraphViz
argument_list|(
literal|""
argument_list|)
return|;
block|}
DECL|method|save (String filepath)
specifier|public
name|void
name|save
parameter_list|(
name|String
name|filepath
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|OutputStreamWriter
name|fout
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|filepath
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
name|fout
operator|.
name|write
argument_list|(
name|generateGraphViz
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|combineEdges (List<Edge> edges)
specifier|public
specifier|static
name|List
argument_list|<
name|Edge
argument_list|>
name|combineEdges
parameter_list|(
name|List
argument_list|<
name|Edge
argument_list|>
name|edges
parameter_list|)
block|{
name|List
argument_list|<
name|Edge
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|Edge
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Edge
name|edge
range|:
name|edges
control|)
block|{
name|boolean
name|found
init|=
literal|false
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
name|ret
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Edge
name|current
init|=
name|ret
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|edge
operator|.
name|sameAs
argument_list|(
name|current
argument_list|)
condition|)
block|{
name|ret
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|current
operator|.
name|combine
argument_list|(
name|edge
argument_list|)
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|edge
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

